package school.java.next.project.rest;

import java.io.File;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import school.java.next.project.dto.TranslateDTO;

@RestController
@RequestMapping("api/v1/translate")
public class TranslateRestController {
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Translate Success"),
			@ApiResponse(responseCode = "500", description = "Error in Translate")
	})
	@PostMapping
	public TranslateDTO translate(@RequestParam File file) {
		return new TranslateDTO();
	}
}
