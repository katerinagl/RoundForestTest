# RoundForestTest

For tasks 1-3
- in root folder run `sbt clean assembly` - you will get target/scala-2.11/RoundForest-assembly-1.0.jar
- run RoundForest-assembly-1.0.jar with `spark-submit target/scala-2.11/RoundForest-assembly-1.0.jar <path-to-/amazon-fine-food-reviews/Reviews.csv>`

For 4 task you need
- run GoogleTranslateEmulator `sbt "runMain GoogleTranslateEmulator"`
- run GoogleTranslateClient `sbt "runMain GoogleTranslateClient <path-to-/amazon-fine-food-reviews/Reviews.csv>"` - and here you will see translates on screen at the end
