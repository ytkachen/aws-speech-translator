package com.cisco.speech

import java.io.File
import java.util.UUID

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.transcribe.AmazonTranscribe
import com.amazonaws.services.transcribe.model._
import com.fasterxml.jackson.databind.ObjectMapper

import scala.collection.JavaConverters._

/**
  * @author Yuri Tkachenko
  */
trait TranscribeAudioJob {

  def uploadAudio(audio: File, name: String)

  def transcribe(audio: File): String
}

class AWSTranscribeAudio(s3: AmazonS3,
                         bucketName: String,
                         transcribe: AmazonTranscribe
                        ) extends TranscribeAudioJob {

  override def uploadAudio(audio: File, name: String): Unit = {
    s3.putObject(bucketName, name, audio)
  }

  private def transcribeJob(fileKeyName: String): String = {

    val job = new StartTranscriptionJobRequest().
      withLanguageCode(LanguageCode.EnUS).
      withMediaFormat(MediaFormat.Wav).
      withOutputBucketName(bucketName).
      withMedia(new Media().
        withMediaFileUri(s"https://s3-us-east-1.amazonaws.com/$bucketName/$fileKeyName")
      ).
      withTranscriptionJobName(s"ja-${UUID.randomUUID().toString}")

    transcribe.startTranscriptionJob(job).
      getTranscriptionJob.
      getTranscriptionJobName
  }

  private def transcribeStatus(jobName: String): String = {
    transcribe.getTranscriptionJob(
      new GetTranscriptionJobRequest().withTranscriptionJobName(jobName)
    ).
      getTranscriptionJob.
      getTranscriptionJobStatus
  }

  def readResult(s3Key: String) = {
    println(s"Read results from key - $s3Key")
    val fileIS = s3.getObject(bucketName, s3Key).getObjectContent
    // read transcript from json
    val objectMapper = new ObjectMapper
    val node = objectMapper.readTree(fileIS)
    node.get("results").get("transcripts").elements.asScala.
      map(_.get("transcript").asText).toSeq.
      head
  }

  private def transcribeResult(jobName: String) = {
    val resultFileKeyPattern = raw"https\://([a-zA-Z0-9\.\-_]+)/([a-zA-Z0-9\-_]+)/(.*)".r

    val transcriptionJob = transcribe.getTranscriptionJob(
      new GetTranscriptionJobRequest().withTranscriptionJobName(jobName)
    ).getTranscriptionJob

    if (transcriptionJob.getTranscriptionJobStatus.equalsIgnoreCase("COMPLETED")) {
      var resultFileUri = transcriptionJob.getTranscript.getTranscriptFileUri

      val key = resultFileUri match {
        case resultFileKeyPattern(_, _, k) => k
      }

      readResult(key)
    } else {
      throw new RuntimeException(transcriptionJob.getFailureReason)
    }
  }

  override def transcribe(audio: File): String = {
    val audioStreamName = UUID.randomUUID().toString

    uploadAudio(audio, s"$audioStreamName.wav")

    val jobName = transcribeJob(s"$audioStreamName.wav")
    // wait until transcribe finishes
    while ( transcribeStatus(jobName).equalsIgnoreCase("IN_PROGRESS")) {
      Thread.sleep(10000L)
    }
    // read results
    transcribeResult(jobName)
  }
}
