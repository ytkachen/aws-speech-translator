# aws-speech-translator
Speech Translator using AWS Services (Transcribe, Translate and Polly)

### build project

`sbt clean package` <br/>
`sbt assembly`

### environment variables
* `AWS_REGION`
* `AWS_ACCESS_KEY`
* `AWS_SECRET_KEY`

These variables could be omitted if you run on EC2 with Instance Role attached

### Run app

`java -jar target/scala-2.12/aws-speech-translator-assembly-0.1.jar <input_audio_wav_file_loc> <output_audio_mp3_file_loc> <target_lang>`

##### Supported target languages:
* Arabic (`ar`)
* Chinese Simplified (`zh`)
* Chinese Traditional (`zh-TW`)
* Czech (`cs`)
* French (`fr`)
* German (`de`)
* Italian (`it`)
* Japanese (`ja`)
* Portuguese (`pt`)
* Russian (`ru`)
* Spanish (`es`)
* Turkish (`tr`)