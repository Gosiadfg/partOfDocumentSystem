import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.blueenergy.document.ApplicationForHolidays;
import pl.blueenergy.document.Document;
import pl.blueenergy.document.DocumentDao;
import pl.blueenergy.document.Question;
import pl.blueenergy.document.Questionnaire;
import pl.blueenergy.organization.User;

public class ProgrammerService {
	
	public void execute(DocumentDao documentDao) {
		List<Document> documents = documentDao.getAllDocumentsInDatabase();
		
		List<Questionnaire> questionnaires = new ArrayList<>();
		List<ApplicationForHolidays> applicationsForHolidays = new ArrayList<>();
			
		//Split a list of documents
		for(Document document : documents) {
			if (document instanceof Questionnaire){
				questionnaires.add((Questionnaire) document);
			}
			else if (document instanceof ApplicationForHolidays){
				applicationsForHolidays.add((ApplicationForHolidays) document);
			}
		}
		
		printQuestionnaires(questionnaires);
		printApplicationsForHolidays(applicationsForHolidays);
		
		//**************************************************************************************
		
		//Get an average number of possible answers in questionnaires
		Integer averageNumberOfPossibleAnswers = getAverageNumberOfPossibleAnswers(questionnaires);
		System.out.println("Average number of possible answers in questionnaires: " + averageNumberOfPossibleAnswers + "\n");
		
		//**************************************************************************************
	
		List<User> usersAppliedForHolidays = new ArrayList<>();	
		List<Character> polishCharacters = Arrays.asList('ą', 'ć', 'ę', 'ł', 'ń', 'ó', 'ś', 'ź', 'ż');
		
		//Create list of users who applied for holidays
		for(ApplicationForHolidays applicationForHolidays : applicationsForHolidays) {
			usersAppliedForHolidays.add(applicationForHolidays.getUserWhoRequestAboutHolidays());
		}
		
		printUsersWhoAppliedForHolidays(usersAppliedForHolidays);
		
		//Check polish characters in logins. Print 'true' if contains or 'false' if not.
		for(User user : usersAppliedForHolidays) {
			boolean hasPolishCharacters = polishCharacters.stream().anyMatch(s -> user.getLogin().contains(s.toString()));
		    System.out.println(user.getLogin() + " HasPolishCharacters: " + hasPolishCharacters);
		}
	    System.out.println();

		//**************************************************************************************

		//Print incorrect dates of holidays
		for(ApplicationForHolidays applicationForHolidays : applicationsForHolidays) {
			if (applicationForHolidays.getSince().compareTo(applicationForHolidays.getTo())>0) {
			    System.out.println(applicationForHolidays.getSince());
			    System.out.println(applicationForHolidays.getTo());
			    System.out.println("Incorrect date of holidays\n");
			}
		}
		
		//**************************************************************************************

		//Write questionnaires to files
		for(Questionnaire questionnaire : questionnaires) {
			FileSystem.writeToFile(questionnaire);
		}
		
		//**************************************************************************************

		//Change salary field
		boolean changeStatus = changeSalaryForSpecificUser(usersAppliedForHolidays, "nowaczka", 3000.0);
	    System.out.println("ChangeStatus: " + changeStatus + "\n");

		printUsersWhoAppliedForHolidays(usersAppliedForHolidays);
	}

	//Change salary field for specific user
	private boolean changeSalaryForSpecificUser(List <User> usersAppliedForHolidays, String login, double newSalary) {
		
		User userToEdit = usersAppliedForHolidays.stream().filter(user -> user.getLogin().equals(login)).findAny().orElse(null);

		try {		
			Field salary = userToEdit.getClass().getDeclaredField("salary");
			salary.setAccessible(true);
			salary.set(userToEdit, newSalary);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
		
	//Print users who applied for holidays
	private void printUsersWhoAppliedForHolidays(List <User> usersAppliedForHolidays) {
		for(User user : usersAppliedForHolidays) {
			System.out.println(user.getLogin());
			System.out.println(user.getName());
			System.out.println(user.getSurname());

			try {
				Field salary = user.getClass().getDeclaredField("salary");
				salary.setAccessible(true);
				System.out.println(salary.get(user));
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//Print questionnaires
	private void printQuestionnaires(List <Questionnaire> questionnaires) {
		for(Questionnaire questionnaire : questionnaires) {
			System.out.println(questionnaire.getTitle() + "\n");
			
			for(Question question : questionnaire.getQuestions()) {
				System.out.println(question.getQuestionText());
				
				for(String possibleAnswer : question.getPossibleAnswers()) {
					System.out.println(possibleAnswer);
				}
				System.out.println();
			}		
		}
	}
	
	//Print applications for holidays
	private void printApplicationsForHolidays(List <ApplicationForHolidays> applicationsForHolidays) {
		for(ApplicationForHolidays applicationForHolidays : applicationsForHolidays) {
			System.out.println(applicationForHolidays.getSince());
			System.out.println(applicationForHolidays.getTo());
			System.out.println(applicationForHolidays.getUserWhoRequestAboutHolidays().getLogin());
			System.out.println(applicationForHolidays.getUserWhoRequestAboutHolidays().getName());
			System.out.println(applicationForHolidays.getUserWhoRequestAboutHolidays().getSurname());
			System.out.println();
		}
	}
	
	//Return average number of possible answers in questionnaires
	private Integer getAverageNumberOfPossibleAnswers(List<Questionnaire> questionnaires) {
		Integer numberOfAllQuestions = 0;
		Integer numberOfAllPossibleAnswers = 0;
		
		for(Questionnaire questionnaire : questionnaires) {
			numberOfAllQuestions+=questionnaire.getQuestions().size();

			for(Question question : questionnaire.getQuestions()) {
				numberOfAllPossibleAnswers+=question.getPossibleAnswers().size();
			}
		}

		return numberOfAllPossibleAnswers/numberOfAllQuestions;
	}
}
