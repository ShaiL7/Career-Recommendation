package net.suncaper;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import scala.Tuple2;

public class RecommendJobs {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("RecommendMovies");
        JavaSparkContext ctx = new JavaSparkContext(conf);

        JavaRDD<String> sourceRatings = ctx.textFile("input/job_skill.txt");


        //filter;
        JavaRDD<String> rRatings=sourceRatings.filter(x->
        {
            String arr[]=x.split(" ");
            if(arr.length<3)
                return false;
            try
            {
                Integer.valueOf(arr[0].trim());
                Integer.valueOf(arr[1].trim());
                Double.valueOf(arr[2]);
                return true;

            }
            catch (Exception e){
                return false;
            }
        })
                .distinct();



        //reduce

        JavaRDD<Rating> unionData = rRatings
                .map(x ->
                {
                    String arr[]=x.split(" ");
                    return new Rating(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), Double.valueOf(arr[2])*10);
                });

       // System.out.println(unionData.count());




        //creat model
        JavaRDD<Rating> arrData[]=unionData.randomSplit(new double[]{0.7,0.3});
        int rank=50;
        int iteration=5;
        double lamda=1 ;

        double min_res=Double.MAX_VALUE;
        MatrixFactorizationModel best_model=null;
        for(int i=0;i<20;i++)
        {
            MatrixFactorizationModel model = ALS.train(unionData.rdd(), rank, iteration, (lamda+i)*0.03, -1);

            JavaRDD<Rating> predict = model.predict(JavaPairRDD.fromJavaRDD(arrData[1].map(x -> new Tuple2<Integer, Integer>(x.user(), x.product()))));
            JavaPairRDD<String, Double> predictRating = JavaPairRDD.fromJavaRDD(predict.map(x ->
                    new Tuple2<>(x.user() + "," + x.product(), x.rating())
            ));
            JavaPairRDD<String, Double> realityRatring = JavaPairRDD.fromJavaRDD(arrData[1].map(x ->
                    new Tuple2<>(x.user() + "," + x.product(), x.rating())
            ));

            JavaRDD<Tuple2<Double, Double>> values = predictRating.join(realityRatring).values();
            double result=Math.sqrt(values.map(x->Math.pow(x._1-x._2,2))
                    .reduce((x,y)->x+y)/(double)values.count());
            if(result<min_res)
            {
                min_res=result;
                best_model=model;

            }
        }


        System.out.println("res:"+min_res);
        best_model.save(ctx.sc(),"output/recommendJob/");
//
//
//
//        MatrixFactorizationModel model = MatrixFactorizationModel.load(ctx.sc(), "output/recommendJob/");
//        Rating[] recommend1 = model.recommendUsers(67, 5);
//        ctx.parallelize(Arrays.asList(recommend1))
//                .foreach(x -> System.out.println(x.user()));
//
//
//        JavaPairRDD<Integer,String> jobDict = JavaPairRDD.fromJavaRDD(ctx.textFile("input/jobDict.txt").map(x->
//        {
//            System.out.println(x);
//             String arr[]=x.split(",");
//             return new Tuple2(Integer.valueOf(arr[0]),arr[1]);
//
//        }));
//        List<String> JobArr = new ArrayList<>();
//        List<Double> RatingArr=new ArrayList<>();
//        for(Integer a=0;a<recommend1.length;a++ )
//        {
//            JobArr.addAll(jobDict.lookup(recommend1[a].user()));
//            RatingArr.add(recommend1[a].rating());
//        }
//
//
//        System.out.println(JobArr.toString()+RatingArr);


    }
}

