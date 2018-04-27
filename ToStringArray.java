package hate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToStringArray {
	
	public static void main(String[] args) {
		
		// Paste from excel into the double quotes
		// Output is the string array format (input for OpenTabs)
		String input = "PB-5548\r\n" + 
				"PB-5630\r\n" + 
				"PB-5641\r\n" + 
				"PB-5546\r\n" + 
				"PB-5660\r\n" + 
				"PB-6247\r\n" + 
				"PB-6344\r\n" + 
				"PB-5999\r\n" + 
				"PB-6821\r\n" + 
				"PB-7493\r\n" + 
				"PB-7887\r\n" + 
				"PB-7275\r\n" + 
				"PB-7505";
		
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
