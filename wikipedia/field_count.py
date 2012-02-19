import sys
import re

def denormalize(s):
    s = re.sub(r'(?<!\\)\\', '', s)
    s = s.decode('utf8','strict')
    s = re.sub('[(),!\'\.\$\/\[\]\^\"]','',s)
    s = re.sub('\-', ' ',s)
    s = re.sub('\s+',' ',s)
    return s

if __name__ == "__main__":
    file_name = sys.argv[1]
    f = open(file_name)
    for l in f:
        l = l.strip()
        l = denormalize(l)
        print l
    f.close()
