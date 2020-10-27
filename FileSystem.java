import java.io.FileWriter;
import java.io.IOException;

import pl.blueenergy.document.Question;
import pl.blueenergy.document.Questionnaire;

public class FileSystem {

	//Write questionnaire to a file
	public static void writeToFile(Questionnaire questionnaire) {
		try {
	      FileWriter writer = new FileWriter(questionnaire.getTitle()+".txt");
	      
	      for(Question question : questionnaire.getQuestions()) {
		      writer.write("Pytanie: " + question.getQuestionText() + "\n");  
		      
		      for(int i = 0; i < question.getPossibleAnswers().size(); i++){
		    	  writer.write(i+1 + ". " + question.getPossibleAnswers().get(i) + "\n");  
		      }
		      writer.write("\n");  
	      }  
	      writer.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
}
