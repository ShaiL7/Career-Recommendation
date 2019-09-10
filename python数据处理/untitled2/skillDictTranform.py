import urllib.request
from urllib import parse
import re
from bs4 import BeautifulSoup
import requests
import json

of1=open("data\\skillDict2.txt","r",encoding="utf-8")
of2=open("data\\skillDict3.txt","w",encoding="utf-8")
index=1
for line in of1.readlines():
   of2.write(str(index)+","+line.strip()+'\n')
   index+=1







