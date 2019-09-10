import urllib.request
from bs4 import BeautifulSoup
import json

# ******************************************************************************
of=open("txtsecond\\6.txt","w",encoding="utf-8")
count=1
# ******************************************************************************
for page in range(1,40):
    try:
        #******************************************************************************
        # url="https://search.51job.com/list/000000,000000,0000,00,9,99,PYTHON,2,"+str(page)+".html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare="
        url="https://search.51job.com/list/000000,000000,0100,00,9,99,%25E6%25B5%258B%25E8%25AF%2595,2,"+str(page)+".html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare="
        response=urllib.request.urlopen(url)
        soup=BeautifulSoup(response.read(),"html.parser",from_encoding="gbk")
        jobNavigater=soup.find_all('p',attrs=['class','t1'])
        companyNavigater=soup.find_all('span',attrs=['class','t2'])
        localNavigater=soup.find_all('span',attrs=['class','t3'])
        salaryNavigater = soup.find_all('span', attrs=['class', 't4'])
    except:
        continue


    for i in range(len(jobNavigater)):
        try:
            jobTitle=jobNavigater[i].get_text().strip().upper()
            #******************************************************************************
            if "测试" not in jobTitle:
                continue
            # print(jobTitle)
            # print("22")
            jobCompany=companyNavigater[i+1].get_text().strip()
            # print(jobCompany)

            jobPlace=localNavigater[i+1].get_text().strip()
            # print(jobPlace)

            jobSalary = salaryNavigater[i + 1].get_text().strip()

            # print(jobSalary)
            url=jobNavigater[i].a['href']
            response = urllib.request.urlopen(url)
            soup=BeautifulSoup(response.read(),"html.parser",from_encoding="gbk")
            jobRequireAdd1 = soup.find_all('div',attrs=['class','mt10'])
            jobRequireAdd2 = soup.find_all('div', attrs=['class', 'share'])
            jobRequireAdd3 = soup.find_all('div', attrs=['class', 'clear'])
            [s.extract() for s in jobRequireAdd1]
            [s.extract() for s in jobRequireAdd2]
            [s.extract() for s in jobRequireAdd3]
            jobRequire=soup.find_all('div',attrs=['class','bmsg job_msg inbox'])
            if len(jobRequire)!=0:
                jobRequire=jobRequire[0].get_text().strip()
                result={'title':jobTitle,'company':jobCompany,'where':jobPlace,'salary':jobSalary,'require':jobRequire}
                of.write(json.dumps(result,ensure_ascii=False)+'\n')
                print("爬完第%d页第%d条"%(page,count))
                count += 1
        except:
            continue





