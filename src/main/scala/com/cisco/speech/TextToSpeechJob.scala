package com.cisco.speech

import java.io.InputStream

import com.amazonaws.services.polly.AmazonPolly
import com.amazonaws.services.polly.model._

import scala.collection.JavaConverters._

/**
  * @author Yuri Tkachenko
  */
trait TextToSpeechJob {
  def synthesizeSpeech(lang: LanguageCode, text: String): InputStream
}

class AWSTextToSpeechJob(tts: AmazonPolly) extends TextToSpeechJob {

  private def langToVoiceId(lang: LanguageCode): String = tts.describeVoices(new DescribeVoicesRequest().withLanguageCode(lang)).
      getVoices.asScala.
      head.
      getId

  def synthesizeSpeech(lang: LanguageCode, text: String): InputStream = {

    val req = new SynthesizeSpeechRequest().
      withLanguageCode(lang).
      withOutputFormat(OutputFormat.Mp3).
      withVoiceId(langToVoiceId(lang)).
      withText(text)

    val res = tts.synthesizeSpeech(req)

    res.getAudioStream
  }
}
