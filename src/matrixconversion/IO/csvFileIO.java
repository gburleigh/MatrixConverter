/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixconversion.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author jingliu5
 */
public class csvFileIO {

    public List readHeader(String filename) throws FileNotFoundException {
        ArrayList<String> headers = new ArrayList<String>();
        Scanner lineScan;
        lineScan = new Scanner(new File(filename));
        Scanner s = new Scanner(lineScan.nextLine());
        s.useDelimiter(",");
        int i = 0;
        if (s.hasNext()) s.next();
        while (s.hasNext()) {
            String character = s.next();
            if (character != null && !character.equals("")) {
                headers.add(character);
            }

        }
        return headers;
    }

    public List readRow(String filename, int rowNum) throws FileNotFoundException {
        Scanner lineScan;
        lineScan = new Scanner(new File(filename));
        Scanner s;
        for (int i = 0; i < rowNum - 1; i++) {
            lineScan.nextLine();
        }
        s = new Scanner(lineScan.nextLine());
        s.useDelimiter(",");
        ArrayList<String> row = new ArrayList<String>();
        while (s.hasNext()) {
            String character = s.next();
            if (!isEmpty(character)) {
                row.add(character);
            }

        }
        return row;
    }

    public List readAll(String filename) throws FileNotFoundException {
        Scanner lineScan;
        lineScan = new Scanner(new File(filename));
        Scanner s;
        List all = new ArrayList();

		List statistics = new ArrayList();

		
        s = new Scanner(lineScan.nextLine());
        s.useDelimiter(",");
        ArrayList<String> headers = new ArrayList<String>();
        if (s.hasNext()) s.next();
        while (s.hasNext()) {
            String character = s.next();
            if (!isEmpty(character)) {
                headers.add(character);
                ArrayList<String> column = new ArrayList<String>();
                all.add(column);
				HashMap columnStaMap = new HashMap();
				statistics.add(columnStaMap);
            }
        }
        
		
		int[] nonEmptyColumn = new int[headers.size() + 1]; // store the non
		// empty row number
		// for each column.
		for (int i = 0; i < headers.size(); i++) {
			nonEmptyColumn[i] = 0;
		}

        // Go through each line of the table and add each cell to the ArrayList
		int total = 0;
        while (lineScan.hasNextLine()) {
            s = new Scanner(lineScan.nextLine());
            s.useDelimiter(", *");
			total++;
			if (s.hasNext()) s.next();
            for (int i = 0; i < all.size(); i++) {            	
                if (s.hasNext()) {
                    String item = s.next();
                    if (!isEmpty(item)) {
                        ArrayList<String> column = (ArrayList<String>) all.get(i);
                        HashMap columnStaMap = (HashMap) statistics.get(i);
                        if (!column.contains(item)) {
        					if (!isEmpty(item)) {
        						nonEmptyColumn[i]++;
        						if (item.indexOf("|") > 0) {
        							int end = 0;
        							String itemleft = item;
        							while (itemleft.indexOf("|") > 0) {
        								end = itemleft.indexOf("|");
        								String curItem = itemleft.substring(0, end);
        								if (!column.contains(curItem)) {
        									column.add(curItem);
        									columnStaMap.put(curItem, 1);
        								} else {
        									int frequency = (Integer) columnStaMap
        											.get(curItem);
        									columnStaMap.remove(curItem);
        									columnStaMap.put(curItem, frequency + 1);
        								}
        								itemleft = itemleft.substring(end + 1);
        							}
        							if (!column.contains(itemleft)) {
        								column.add(itemleft);
        								columnStaMap.put(itemleft, 1);
        							} else {
        								int frequency = (Integer) columnStaMap
        										.get(itemleft);
        								columnStaMap.remove(itemleft);
        								columnStaMap.put(itemleft, frequency + 1);
        							}
        						} else if (!column.contains(item)) {
        							column.add(item);
        							columnStaMap.put(item, 1);
        						} else {
        							int frequency = (Integer) columnStaMap.get(item);
        							columnStaMap.remove(item);
        							columnStaMap.put(item, frequency + 1);
        						}
        					}
                        }
                    }
                }
            }
        }
        
        
        nonEmptyColumn[headers.size()] = total;
		all.add(nonEmptyColumn);
		all.add(statistics);
        return all;
    }
    
    
    private boolean isEmpty(String item){
    	if (item == null || item.equals("") || item.length() ==0 || item.equals("NA") || item.equals("N/A") )
    		return true;
    	else 
    		return false;
    }

    
}
