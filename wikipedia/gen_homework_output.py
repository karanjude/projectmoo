
def get_doc_stats(file_name):
    f = open(file_name)
    l = f.readline()
    l = l.split()
    l = map(lambda x:x.split(':'), l)
    r = {}
    for i in l:
        r[i[0]] = int(i[1])
    return r

def process(f1, f2, doc_stats, id):
    print "%s %s %s" % (id, f2, doc_stats[f1])
    file_name = "data/%s_stats.txt" % (f1)
    f = open(file_name)
    length = int(f.readline().strip())
    tokens = f.readline().strip().split()
    tokens = map(lambda x: x.split(':'), tokens)
    f.close()

    print "%s %s %s" % (id, f2, length)
    for i in range(0,len(tokens)):
        token_tf = int(tokens[i][1])
        token_p = float(token_tf) / float(length)
        token_pr = token_p * (i+1)
        print "%s %s %s %s %s %s %s" % (id, f2, i+1, tokens[i][0], token_tf, token_p, token_pr)

if __name__ == "__main__":
    fields = ['title','h','a','all']
    field_name = ['title','heading','anchor','all']
    doc_stats = get_doc_stats("data/document_stats.txt")

    for i in range(0,len(fields)):
        process(fields[i], field_name[i], doc_stats, "5577894215")
