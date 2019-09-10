package com.example.demo.service;

import com.example.demo.controller.IndexController;
import org.apache.avro.reflect.MapEntry;
import org.apache.hadoop.mapreduce.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import scala.Tuple2;

import java.io.*;
import java.util.*;

@Service
public class RecommendJobService {
    //用户注册逻辑
    SparkConf conf = new SparkConf().setMaster("local").setAppName("RecommendMovies");
    JavaSparkContext ctx = new JavaSparkContext(conf);
    Map<Integer, String> skillmap = new HashMap<>();

    public List<Tuple2<String, Double>> recommandjob(Map<String, String> id_score, ArrayList<String> idJobArr) {
        ArrayList<String> idArr = new ArrayList<>();
        ArrayList<Double> scoreArr = new ArrayList<>();
        id_score.forEach((id, score) ->
        {
            idArr.add(id);
            scoreArr.add(Double.valueOf(score));
        });
        //  JavaRDD<String> sourceRatings = ctx.textFile("job_skill.txt");

        MatrixFactorizationModel model = MatrixFactorizationModel.load(ctx.sc(), "recommendJob/");

        JavaPairRDD<Integer, String> jobDict = JavaPairRDD.fromJavaRDD(ctx.textFile("jobDict.txt").map(x ->
        {
            String arr[] = x.split(",");
            return new Tuple2(Integer.valueOf(arr[0]), arr[1]);

        }));
        Map<Integer, Double> result = new HashMap<>();
        for (int i = 0; i < idArr.size(); i++) {
            System.out.println("dfsdf" + idArr.get(i));
            Rating[] recommend = model.recommendUsers(Integer.valueOf(idArr.get(i)), 5);
//            //对rating 进行归一化,提高准确率
//            Double totalScore=0.0;
//            for(Integer b=0;b<recommend.length;b++)
//                totalScore+=recommend[b].rating();
            for (Integer a = 0; a < recommend.length; a++) {

                Double jobRating = recommend[a].rating();
                Integer jobId = recommend[a].user();
                // String jobname = jobDict.lookup(recommend[a].user()).get(0);

                if (result.containsKey(jobId))
                    result.put(jobId, jobRating * scoreArr.get(i) + result.get(jobId));
                else
                    result.put(jobId, jobRating * scoreArr.get(i));

            }

        }


        List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(result.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            //降序排序
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });

        for (Map.Entry<Integer, Double> mapping : list) {
            System.out.println(mapping.getKey() + ":" + mapping.getValue());
        }
        list = list.subList(0, 5);
        List<Tuple2<String, Double>> list2 = new ArrayList<>();
        list.forEach(x ->
        {
            idJobArr.add(String.valueOf(x.getKey()));
            String jobname = jobDict.lookup(x.getKey()).get(0);
            list2.add(new Tuple2<>(jobname, x.getValue()));
        });

        return list2;
    }


    public ArrayList<ArrayList<Tuple2<String, String>>> getExtraSkill(ArrayList<String> idJobArr, ArrayList<String> idSkillArr) {
        ArrayList<String> lines = new ArrayList<>();
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
            String skillDictPath = "job_skill.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(skillDictPath); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while ((line = br.readLine()) != null)
                lines.add(line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<Tuple2<String, String>>> result = new ArrayList<>();
        for (String idJob : idJobArr) {
            int count = 3;
            int index = 0;
            ArrayList<Tuple2<String, String>> extraSkill = new ArrayList<>();
            while (count > 0) {
                index++;
                String arr[] = lines.get((Integer.valueOf(idJob) - 1) * 15 + index - 1).split(" ");
                if (Arrays.asList(idSkillArr).contains(arr[1]))
                    continue;
                else {
                    String[] idArr = {arr[1]};
                    extraSkill.add(new Tuple2<>(getSkillById(idArr).get(0), arr[2]));
                    count--;
                }

            }
            result.add(extraSkill);
        }

        return result;
    }


    public ArrayList<String> getSkillById(String idArr[]) {
        ArrayList<String> skillName = new ArrayList<>();

        for (String id : idArr) {
            skillName.add(skillmap.get(Integer.valueOf(id)));
        }
        System.out.println(skillName);
        return skillName;
    }

    public void initSkill_IdDict() {
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
            String skillDictPath = "skillDict.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(skillDictPath); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                skillmap.put(Integer.valueOf(arr[0]), arr[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

