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
            if(Constants.DEBUG == true) {
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
            if(Constants.DEBUG == true) {
                System.out.println("Name: " + name + ", action: " + action + " with " + params.length + " param(s).");
            }
        }
    }

    private Map<String, Gate> gates = new HashMap<String, Gate>();
    private Map<String, List<String>> gateRevDeps = new HashMap<String, List<String>>();

    public Circuit(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Gate gate = new Gate(line);
                gates.put(gate.name, gate);
                for (String param : gate.params) {
                    boolean isNum = param.matches("^\\d+$");
                    if (isNum  == false) {
                        if (gateRevDeps.get(gate.name) == null) {
                            gateRevDeps.put(gate.name, new ArrayList<String>());
                        }
                        // If it's not a literal add it's back dependency
                        gateRevDeps.get(gate.name).add(param);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("Encountered IO error.");
        }
        System.out.println(gateRevDeps.get("hr"));
        // Resolve values
        for (String key : gates.keySet()) {
            Gate gate = gates.get(key);
            int value = resolveValues(gate);
            if(Constants.DEBUG == true) {
                System.out.println(key + ": " + value);
            }
        }
    }

    public int get(String gateKey){
        return gates.get(gateKey).value;
    }

    private int resolveValues(Gate gate) {
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
                        deps[i] = resolveValues(dep);
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

    private void bustDeps() {

    }
}
