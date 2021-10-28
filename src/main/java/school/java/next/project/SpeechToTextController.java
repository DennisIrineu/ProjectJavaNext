package school.java.next.project;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;

@RestController
@RequestMapping("/api/v1/translate")
public class SpeechToTextController{

	private CredentialsProvider credentialProvider;
	private SpeechSettings settings = null;

	@PostConstruct
	public void initialize() throws IOException {
		CredentialsProvider credentialProviders = FixedCredentialsProvider.create(ServiceAccountCredentials
				.fromStream(new FileInputStream("src/main/resources/google_credentials.json")));
		settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialProviders).build();
	}

	@GetMapping(path = {"/speech"})
	public Message convertSpeetchToText() throws Exception{
		try(SpeechClient client = SpeechClient.create(settings)){
			RecognitionConfig.Builder builder = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setLanguageCode("en-US")
					.setEnableAutomaticPunctuation(true)
					.setEnableWordTimeOffsets(true)
					.setSampleRateHertz(48000)
					.setAudioChannelCount(1);			
			
			RecognitionConfig config = builder.build();			
			
			RecognitionAudio audio = RecognitionAudio.newBuilder().setUri("gs://javaproject/Marvin-1.wav").build();
			
			OperationFuture<LongRunningRecognizeResponse,LongRunningRecognizeMetadata> response = client.longRunningRecognizeAsync(config, audio);
			
			while(!response.isDone()) {
				Thread.sleep(10000);
			}
			
			java.util.List<SpeechRecognitionResult> speechResults = response.get().getResultsList();
			
			
			StringBuilder transciption = new StringBuilder();
			for(SpeechRecognitionResult result : speechResults) {
				com.google.cloud.speech.v1.SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				transciption.append(alternative.getTranscript());
			}
			
			Message message = new Message();
			message.setContents(transciption.toString());
			return message;
			
		}
	}

}
