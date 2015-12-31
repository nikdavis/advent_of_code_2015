require 'digest'

# PART 1
input = "bgvyzdsv"
md5 = ""
i = 1
j = 0

while(true)
  md5 = Digest::MD5.hexdigest(input + i.to_s)
  if md5[0..4] == "00000"
    break
  end
  j += 1
  if (j % 100000) == 0
    puts "Checked #{j} hashes."
  end
  i += 1
end

puts "Checked #{i} hashes total (text = bgvyzdsv#{i}, hash = #{md5})"


# PART 2
md5 = ""
i = 1
j = 0

while(true)
  md5 = Digest::MD5.hexdigest(input + i.to_s)
  if md5[0..5] == "000000"
    break
  end
  j += 1
  if (j % 100000) == 0
    puts "Checked #{j} hashes."
  end
  i += 1
end

puts "Checked #{i} hashes total (text = bgvyzdsv#{i})"