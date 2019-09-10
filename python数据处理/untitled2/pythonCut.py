import json
import string
import jieba.analyse

job_skillFile=open("data\\job_skill.txt","w",encoding="utf-8")
skillDictFile=open("data\\skillDict2.txt","r",encoding="utf-8")
jobDictFile=open("data\\jobDict.txt","r",encoding="utf-8")
skillSetFile=open("data\\skillSet.txt","w",encoding="utf-8")


add_punc='123456789、【】“”：；（）《》‘’{}？！⑦()、%^>℃：.”“^-——=擅长于的&#@￥'
all_punc= string.punctuation + add_punc
all_punc=all_punc.replace('+','')
all_punc=all_punc.replace('#','')

inwords=[]
# add dict
for line in skillDictFile.readlines():
    inwords.append(line.strip())
jieba.load_userdict('data\\skillDict2.txt')

# creat skill dict
skillDict={}
for index in range(len(inwords)):
    skillDict.update({inwords[index]:index+1})

#
skill=set()
for index in range(23):
    strpath="txt\\"+str(index+1)+".txt"
    jobInfoFile=open(strpath,"r",encoding="utf-8")

    final = ''
    lines = jobInfoFile.readlines()
    for line in lines:
        require=json.loads(line)['require'].upper()
        require = ''.join(c for c in require if c not in all_punc)
        resultlist=jieba.cut(require)
        for seg in resultlist:
            if seg in inwords:
                final += seg+" "
    tags = jieba.analyse.extract_tags(final, topK=15, withWeight=True)

    total=0.0
    for i in tags:
        total+=i[1]
    for i in tags:
        str1=str(index+1)+' '+str(skillDict[i[0]])+' '+str(i[1]/total)
        skill.add(tuple([skillDict[i[0]],i[0]]))
        job_skillFile.write(str1+"\n")
    job_skillFile.write("\n\n\n\n")

for i in skill:
    skillSetFile.write(str(i)+"\n")



