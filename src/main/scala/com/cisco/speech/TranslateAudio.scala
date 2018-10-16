package com.cisco.speech

import java.io.File

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder
import com.amazonaws.services.translate.AmazonTranslateClientBuilder

/**
  * @author Yuri Tkachenko
  */
object TranslateAudio extends App {

  if (args.length != 2) {
    println("Invalid input parameters")
  }

  val inputAudio =  args(0)
  val outputFile = args(1)

  // create S3 client
  val s3Client = AmazonS3ClientBuilder.defaultClient()
  val transcribeClient = AmazonTranscribeClientBuilder.defaultClient()
  val translateClient = AmazonTranslateClientBuilder.defaultClient()

  val job = new AWSTranscribeAudio(s3Client, "iec4-speech-translator", transcribeClient)

  val text = job.transcribe(new File(inputAudio))

  println(text)

  val targetLang = "fr"

  val frText = new AWSTranslateTextJob(translateClient, "en").translate(text, targetLang)

  println(frText)

}
