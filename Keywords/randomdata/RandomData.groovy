package randomdata

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class RandomData {

	@Keyword
	def generateRandomFirstName() {
		String[] firstWord = [
			"John",
			"Emily",
			"Michael",
			"Sarah",
			"David",
			"Jessica"
		]
		String[] lastWord = [
			"Hugo",
			"Luca",
			"Anna",
			"Matteo",
			"Elena"
		]

		Random random = new Random()
		String firstName = firstWord[random.nextInt(firstWord.length)]
		String lastName = lastWord[random.nextInt(lastWord.length)]

		return firstName + " " + lastName
	}

	@Keyword
	def generateRandomLastName() {
		String[] firstWord = [
			"Smith",
			"Johnson",
			"Brown",
			"Williams",
			"Taylor"
		]
		String[] lastWord = [
			"Müller",
			"Rossi",
			"Dubois",
			"Kovács",
			"Novak"
		]

		Random random = new Random()
		String firstName = firstWord[random.nextInt(firstWord.length)]
		String lastName = lastWord[random.nextInt(lastWord.length)]

		return firstName + " " + lastName
	}
	
	@Keyword
	def generateRandomCompanyName() {
		String[] prefixes = [
			"Tech", 
			"Global", 
			"Innovative", 
			"Smart", 
			"NextGen", 
			"Pioneer", 
			"Dynamic", 
			"Future", 
			"BlueSky", 
			"Elite"
		]
		String[] suffixes = [
			"Solutions", 
			"Industries", 
			"Systems", 
			"Corp", 
			"Enterprises", 
			"Technologies", 
			"Group", 
			"Ventures", 
			"Labs", 
			"LLC"
		]
		
		Random rand = new Random()
		
		String randomPrefix = prefixes[rand.nextInt(prefixes.size())]
		String randomSuffix = suffixes[rand.nextInt(suffixes.size())]
		
		return randomPrefix + " " + randomSuffix
			
	}
	
	@Keyword
	def generateRandomAddress() {
		Random rand = new Random()
		int houseNumber = rand.nextInt(9999) + 1

		String [] streetNames = [
			"Maple", 
			"Oak", 
			"Pine", 
			"Cedar", 
			"Elm", 
			"Birch", 
			"Willow", 
			"Sunset", 
			"River", 
			"Hilltop"
		]
		
		String [] streetTypes = [
			"St", 
			"Ave", 
			"Blvd", 
			"Ln", 
			"Rd", 
			"Dr", 
			"Ct", 
			"Pl", 
			"Way", 
			"Terrace"
		]

		String randomStreetName = streetNames[rand.nextInt(streetNames.size())]
		String randomStreetType = streetTypes[rand.nextInt(streetTypes.size())]

		return  houseNumber + " " + randomStreetName + " " + randomStreetType

	}
	
	@Keyword
	def generateRandomUnit() {
		Random rand = new Random()
		int unitNumber = rand.nextInt(500) + 1

		String [] unitTypes = [
			"Apt", 
			"Suite", 
			"Unit", 
			"Floor", 
			"Room"
		]
		
		String randomUnitType = unitTypes[rand.nextInt(unitTypes.size())]

		return randomUnitType + " " + unitNumber

	}
	
	@Keyword
	def generateRandomPostalCode() {
		Random rand = new Random()

		int postalCode = rand.nextInt(90000) + 10000 

		return postalCode.toString()
	}
	
	@Keyword
	def generateRandomCityIndonesia() {
		String [] cities = [
			"Jakarta", 
			"Surabaya", 
			"Bandung", 
			"Medan", 
			"Semarang",
			"Makassar", 
			"Palembang", 
			"Depok", 
			"Tangerang", 
			"Bekasi",
			"Yogyakarta", 
			"Malang", 
			"Padang", 
			"Batam", 
			"Bogor", 
		]

		Random rand = new Random()
		return cities[rand.nextInt(cities.size())]

	}
	
	@Keyword
	def generateRandomPhoneNumber() {
		Random rand = new Random()

		String [] prefixes = [
			"0811", 
			"0812", 
			"0813", 
			"0821", 
			"0822", 
			"0823",					 
			"0851", 
			"0852", 
			"0853", 
			"0856", 
			"0857"
		]

		String prefix = prefixes[rand.nextInt(prefixes.size())]

		String randomNumber = ""
		for (int i = 0; i < 7; i++) {
			randomNumber += rand.nextInt(10)
		}

		return prefix + randomNumber

	}
	
	@Keyword
	def generateRandomEmail() {
		Random rand = new Random()

		String characters = "abcdefghijklmnopqrstuvwxyz0123456789"
		String username = ""
		int usernameLength = 5 + rand.nextInt(6) // Between 5 and 10 characters
		for (int i = 0; i < usernameLength; i++) {
			username += characters.charAt(rand.nextInt(characters.length()))
		}

		List<String> domains = ["gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "mail.com", "example.com"]

		String domain = domains[rand.nextInt(domains.size())]

		return username + "@" + domain

	}
	
	@Keyword
	def generateRandomOrderNotes() {
		String [] notes = [
			"Please leave the package at the front door.",
			"Deliver to the reception desk.",
			"Call before delivery.",
			"Leave with a neighbor if not home.",
			"Ring the doorbell twice upon arrival.",
			"Place the package inside the mailbox.",
			"Hand it over to security at the gate.",
			"Drop the parcel at the apartment lobby.",
			"Do not leave in direct sunlight.",
			"Deliver between 9 AM and 5 PM only."
		]

		Random rand = new Random()
		return notes[rand.nextInt(notes.size())]
		
	}
}
