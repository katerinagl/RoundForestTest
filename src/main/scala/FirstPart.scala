/**
  * Created by katerinaglushchenko on 6/22/17.
  */

import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel

import scala.util.matching.Regex

object FirstPart {

  def main(args: Array[String]): Unit = {
    if (args.size == 1) {
      val path = args(0)

      val conf = new SparkConf().setAppName("Test").setMaster("local[*]")

      val spark = SparkSession.builder
        .config(conf)
        .getOrCreate()

      val sc = spark.sparkContext

      val sqlContext = new SQLContext(sc)
      import sqlContext.implicits._

      val data = spark
        .read
        .format("csv").option("header", true)
        .load(path).persist(StorageLevel.MEMORY_AND_DISK)

      //        1) Finding 1000 most active users (profile names)
      data.groupBy("ProfileName").count().orderBy(desc("count")).limit(1000).select("ProfileName").orderBy("ProfileName").show(1000, truncate = false)

      //        2) Finding 1000 most commented food items (item ids).
      data.groupBy("ProductId").count().orderBy(desc("count")).limit(1000).select("ProductId").orderBy("ProductId").show(1000)

      //        3) Finding 1000 most used words in the reviews
      val clean = udf { (text: Iterable[String]) =>
        val pattern = "[^{a-zA-Z }]"
        text.map(str => str.replaceAll(pattern, "").replaceAll(" +", " ").toLowerCase).toSeq
      }

      val rdd = data.select(clean(collect_list("Text"))).rdd.flatMap(r => r.getAs[Iterable[String]](0))
      val zipped = rdd.flatMap(r => r.split(" ")).map(r => r -> 1)
      val result = zipped.reduceByKey(_ + _).toDF("word", "amount").orderBy(desc("amount"), $"word").limit(1000).select("word")
      result.show(1000)

    } else {
      println("Please add review.csv path as agrument ")
    }
  }
}