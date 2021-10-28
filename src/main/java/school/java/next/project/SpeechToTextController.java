package school.java.next.project;

import static com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding.LINEAR16;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.services.translate.Translate.Languages.List;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
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
					.setEnableAutomaticPunctuation(true).setEnableWordTimeOffsets(true);
			
			
			// builder.setModel("src/main/resources/sync-request.json"); //  default
			builder.setAudioChannelCount(2);
			builder.setSampleRateHertz(48000);
			
			RecognitionConfig config = builder.build();
			
			RecognitionAudio audio = RecognitionAudio.newBuilder().setUri("gs://javaproject/test-audio.wav").build();
			
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

	@GetMapping("/speech-2")
	public void convertSpeetchToText2() throws IOException {
		try (SpeechClient speechClient = SpeechClient.create(settings)) {
			String gcsUri = "gs://javaproject/Marvin-1.wav";

			RecognitionConfig config =
			      RecognitionConfig.newBuilder()
			          .setEncoding(LINEAR16)
			          .setSampleRateHertz(48000)
			          .setLanguageCode("en-US")
		          .build();

			RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

			RecognizeResponse response = speechClient.recognize(config, audio);

			java.util.List<SpeechRecognitionResult> results = response.getResultsList();

			for (SpeechRecognitionResult result : results) {
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
			}
	    }
	}
}
