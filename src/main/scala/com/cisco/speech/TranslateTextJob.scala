package com.cisco.speech

import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.model.TranslateTextRequest

/**
  * @author Yuri Tkachenko
  */
trait TranslateTextJob {
  def translate(text: String, lang: String): String
}

class AWSTranslateTextJob(translate: AmazonTranslate,
                          srcLang: String) extends TranslateTextJob {

  override def translate(text: String, lang: String): String = {
    val result = translate.translateText(new TranslateTextRequest().
      withSourceLanguageCode(srcLang).
      withTargetLanguageCode(lang).
      withText(text)
    )

    result.getTranslatedText
  }
}
