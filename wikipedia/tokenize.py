import sys
import re

def denormalize(s):
    s = re.sub(r'(?<!\\)\\', '', s)
    s = s.decode('utf8','strict')
    s = re.sub('[(),!\'\.\$\/\[\]\^\"]','',s)
    s = re.sub('\-', ' ',s)
    s = re.sub('\s+',' ',s)
    return s

def tokenize(l):
    l = l.strip()
    l = denormalize(l)
    l = l.lower()
    return l.split()
    

if __name__ == "__main__":
    file_name = sys.argv[1]
    f = open(file_name)
    for l in f:
        print tokenize(l)
    f.close()
