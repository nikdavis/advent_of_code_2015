package com.nikolasdavis; /**
 * Created by ndavis on 12/31/15.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nikolasdavis.Constants;

public class Circuit {
    public class Gate {
        public String name;
        public String action;
        public String[] params;
        public int value = -1;              // -1 symbolizes not set; all values will be 0 or greater

        public Gate(String gateText) {
            if(Constants.DEBUG) {
                System.out.println(gateText);
            }
            String[] tmp = gateText.split(" -> ");
            String[] command = tmp[0].split(" ");
            name = tmp[tmp.length-1];

            if(command.length == 1) {
                params = new String[] {command[0]};
                if(params[0].matches("\\d+")) {
                    action = "LITERAL";
                    value = Integer.parseInt(params[0]);
                } else {
                    action = "WIRE";
                }
            } else if(command.length == 2) {
                action = command[0];
                params = new String[] {command[1]};
            } else if(command.length == 3) {
                action = command[1];
                params = new String[] {command[0], command[2]};
            }
            if(Constants.DEBUG) {
                System.out.println("Name: " + name + ", action: " + action + " with " + params.length + " param(s).");
            }
        }
    }

    private Map<String, Gate> gates = new HashMap<String, Gate>();
    private Map<String, List<String>> gatesRevDeps = new HashMap<String, List<String>>();

    public Circuit(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Gate gate = new Gate(line);
                gates.put(gate.name, gate);
                for (String param : gate.params) {
                    // Param is not a literal, then store this gate as a upstream dep
                    if (param.matches("^\\d+$") != true) {
                        if (gatesRevDeps.get(param) == null) {
                            gatesRevDeps.put(param, new ArrayList<String>());
                        }
                        // If it's not a literal add it's back dependency
                        gatesRevDeps.get(param).add(gate.name);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("Encountered IO error.");
        }
        if(Constants.DEBUG) {
            for (Map.Entry entry : gatesRevDeps.entrySet()) {
                System.out.println("deps of " + entry.getKey() + ": " + entry.getValue());
            }
        }
        resolveValues();
    }

    public int get(String gateKey){
        return gates.get(gateKey).value;
    }

    // allows setting to an int value
    public void set(String gateKey, int value) {
        // change value, destroy deps, and bust upstream cached values
        Gate gate = gates.get(gateKey);
        gate.value = value;
        gate.params = new String[] {String.valueOf(value)};
        bustUpstreamDeps(gate, new ArrayList<String>());
        resolveValues();
    }

    private void resolveValues() {
        // Resolve values
        for (String key : gates.keySet()) {
            Gate gate = gates.get(key);
            int value = resolveValuesRec(gate);
            if(Constants.DEBUG) {
                System.out.println(key + ": " + value);
            }
        }
    }

    private int resolveValuesRec(Gate gate) {
        if (gate.value < 0) {
            int[] deps = new int[gate.params.length];
            for (int i = 0; i < gate.params.length; i++) {
                if (gate.params[i].matches("^\\d+$")) {
                    deps[i] = Integer.parseInt(gate.params[i]);
                } else {
                    Gate dep = gates.get(gate.params[i]);
                    if (dep.value >= 0) {
                        deps[i] = dep.value;
                    } else {
                        deps[i] = resolveValuesRec(dep);
                    }
                }
            }

            if (gate.action.equals("AND")) {
                gate.value = deps[0] & deps[1];
            } else if (gate.action.equals("OR")) {
                gate.value = deps[0] | deps[1];
            } else if (gate.action.equals("NOT")) {
                gate.value = (~deps[0]) & 0xFFFF;
            } else if (gate.action.equals("LSHIFT")) {
                gate.value = (deps[0] << deps[1]) & 0xFFFF;
            } else if (gate.action.equals("RSHIFT")) {
                gate.value = (deps[0] >> deps[1]) & 0xFFFF;
            } else if (gate.action.equals("LITERAL") || gate.action.equals("WIRE")) {
                gate.value = deps[0];
            }
        }

        return gate.value;
    }

    private void bustUpstreamDeps(Gate gate, List<String> uniqueDepList) {
        List<String> depList = gatesRevDeps.get(gate.name);
        if(depList != null) {
            for (String depName : depList) {
                if (!uniqueDepList.contains(depName)) {
                    Gate dep = gates.get(depName);
                    dep.value = -1;
                    uniqueDepList.add(depName);
                    bustUpstreamDeps(dep, uniqueDepList);
                }
            }
        }
    }
}
