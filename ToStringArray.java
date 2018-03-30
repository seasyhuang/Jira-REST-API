package hate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToStringArray {
	
	public static void main(String[] args) {
		
		// Paste from excel into the double quotes
		// Output is the string array format (input for OpenTabs)
		String input = "APP-8003\r\n" + 
				"APP-7976\r\n" + 
				"APP-7972\r\n" + 
				"APP-7977\r\n" + 
				"APP-7982";
		
		String result = convert(input);
		
		System.out.println("---------------");
		
		System.out.println(result);
	}
	
	public static String convert(String input) {
		
		System.out.println(input);
		
		String output = input.replaceAll("\r", " ").replaceAll("\n", " "); 
		output = output.replaceAll("  ", ",");
		
		List<String> temp = Arrays.asList(output.split(","));
		System.out.println("---------------");
		System.out.println(temp);
		
		Function<String,String> addQuotes = s -> "\n\"" + s + "\"";

		String result = temp.stream()
		  .map(addQuotes)
		  .collect(Collectors.joining(", "));
		
		return result;
	}
}
