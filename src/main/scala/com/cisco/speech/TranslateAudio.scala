package com.cisco.speech

import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}

import com.amazonaws.services.polly.AmazonPollyClientBuilder
import com.amazonaws.services.polly.model.LanguageCode
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder
import com.amazonaws.services.translate.AmazonTranslateClientBuilder

/**
  * @author Yuri Tkachenko
  */
object TranslateAudio extends App {

  if (args.length != 3) {
    println("Invalid input parameters")
  }

  val inputAudio =  args(0)
  val outputFile = args(1)
  val targetLang = args(2)

  // create S3 client
  val s3Client = AmazonS3ClientBuilder.defaultClient()
  val transcribeClient = AmazonTranscribeClientBuilder.defaultClient()
  val translateClient = AmazonTranslateClientBuilder.defaultClient()
  val ttsClient = AmazonPollyClientBuilder.defaultClient()

  val job = new AWSTranscribeAudio(s3Client, "iec4-speech-translator", transcribeClient)

  val text = job.transcribe(new File(inputAudio))

  println(text)

  val targetText = new AWSTranslateTextJob(translateClient, "en").translate(text, targetLang)

  println(targetText)

  val tLang = LanguageCode.values().filter(l => l.toString.startsWith(targetLang)).head

  val outputAudio = new AWSTextToSpeechJob(ttsClient).synthesizeSpeech(tLang, targetText)

  Files.copy(outputAudio, Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING)
  println(s"Written file to $outputFile")
}
