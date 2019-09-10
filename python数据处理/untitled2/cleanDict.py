source=open("data\\reduce_data","r",encoding='utf-8')
output=open("data\\skillDict2.txt",'w',encoding='utf-8')
dict=set()
for line in source.readlines():
    line=line.strip()
    line=line.upper()
    arr=line.split(' ')[2::]
    for i in arr:
        dict.add(i)
for i in dict:
    output.write(i+'\n')