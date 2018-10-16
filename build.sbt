name := "aws-speech-translator"

version := "0.1"

scalaVersion := "2.12.7"

lazy val awsSdkV = "1.11.427"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % awsSdkV,
  "com.amazonaws" % "aws-java-sdk-transcribe" % awsSdkV,
  "com.amazonaws" % "aws-java-sdk-translate" % awsSdkV,
  "com.amazonaws" % "aws-java-sdk-polly" % awsSdkV
)