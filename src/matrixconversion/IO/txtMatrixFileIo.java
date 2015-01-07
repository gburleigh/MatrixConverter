/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixconversion.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import matrixconversion.util.StringPattern;

/**
 * 
 * @author jingliu5
 */
public class txtMatrixFileIo {

	public List readHeader(String filename) throws IOException {
		List headers = readHeader(filename, false);
		return headers;
	}
	
    private boolean isEmpty(String item){
    	if (item == null || item.equals("") || item.length() ==0 || item.equals("NA") || item.equals("N/A") )
    		return true;
    	else 
    		return false;
    }


	public List readHeader(String filename, boolean skipFirstColumn)
			throws IOException {
		ArrayList<String> headers = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));

		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
			int idx = 0;
			int idxStart = 0, idxEnd = 0;
			String item;
			while (idx < sCurrentLine.length()) {
				idxStart = idx;
				idxEnd = sCurrentLine.indexOf("\t", idx);
				if (idxEnd < 0)
					idxEnd = sCurrentLine.length();
				item = sCurrentLine.substring(idxStart, idxEnd);
				if (!(skipFirstColumn && idx == 0)) {
					headers.add(item);
				}
				idx = idxEnd + 1;
			}
			System.out.println(sCurrentLine);
			break;
		}

		br.close();
		return headers;
	}

	public List readAll(String filename) throws IOException {
		List all = readAll(filename, false);
		return all;
	}

	public List readAll(String filename, boolean skipFirstColumn)
			throws IOException {
		List all = new ArrayList();

		ArrayList<String> headers = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));

		String sCurrentLine;
		sCurrentLine = br.readLine();
		int idx = 0;
		int idxStart = 0, idxEnd = 0;
		String item;

		List statistics = new ArrayList();
		while (idx < sCurrentLine.length()) {
			idxStart = idx;
			idxEnd = sCurrentLine.indexOf("\t", idx);
			if (idxEnd < 0)
				idxEnd = sCurrentLine.length();
			item = sCurrentLine.substring(idxStart, idxEnd);
			if (!(skipFirstColumn && idx == 0)) {
				headers.add(item);
				ArrayList<String> column = new ArrayList<String>();
				all.add(column);
				HashMap columnStaMap = new HashMap();
				statistics.add(columnStaMap);
			}
			idx = idxEnd + 1;
		}
		System.out.println(sCurrentLine);

		int[] nonEmptyColumn = new int[headers.size() + 1]; // store the non
															// empty row number
															// for each column.
		for (int i = 0; i < headers.size(); i++) {
			nonEmptyColumn[i] = 0;
		}

		int total = 0;
		while ((sCurrentLine = br.readLine()) != null) {
			idx = 0;
			idxStart = 0;
			idxEnd = 0;
			int i = 0;
			total++;
			while (idx < sCurrentLine.length() && i < all.size()) {
				idxStart = idx;
				idxEnd = sCurrentLine.indexOf("\t", idx);
				if (idxEnd < 0)
					idxEnd = sCurrentLine.length();
				item = sCurrentLine.substring(idxStart, idxEnd);
				if (!(skipFirstColumn && idx == 0)) {
					ArrayList<String> column = (ArrayList<String>) all.get(i);
					HashMap columnStaMap = (HashMap) statistics.get(i);
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
					i++;
				}
				idx = idxEnd + 1;
			}
			// System.out.println(sCurrentLine);
		}
		br.close();
		nonEmptyColumn[headers.size()] = total;
		all.add(nonEmptyColumn);
		all.add(statistics);
		return all;
	}

	public void saveTxt(String filename, String outfilename,
			HashMap mappingRuleMap) throws IOException {
		saveTxt(filename, outfilename, mappingRuleMap, true);
	}

	public void saveTxt(String filename, String outfilename,
			HashMap mappingRuleMap, boolean saveAll) throws IOException {

		BufferedWriter bw;

		ArrayList<String> headers = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));

		bw = new BufferedWriter(new FileWriter(outfilename));

		// output header of table
		String sCurrentLine;
		sCurrentLine = br.readLine();
		int idx = 0;
		int idxStart = 0, idxEnd = 0;
		String item;
		while (idx < sCurrentLine.length()) {
			idxStart = idx;
			idxEnd = sCurrentLine.indexOf("\t", idx);
			if (idxEnd < 0)
				idxEnd = sCurrentLine.length();
			item = sCurrentLine.substring(idxStart, idxEnd);
			headers.add(item);
			SortedMap featureMap = (SortedMap) mappingRuleMap.get(item);
			if (idxStart == 0) {
				bw.write(item + "\t");
			}
			if (!(featureMap == null || featureMap.isEmpty())) {
				bw.write(item + "\t");
			} else if (saveAll) {
				bw.write(item + "\t");// 输出字符
			}
			bw.flush();
			idx = idxEnd + 1;
		}
		bw.write("\r\n");
	//	bw.newLine();// 换行
		bw.flush();
		System.out.println(sCurrentLine);

		// output table contents
		while ((sCurrentLine = br.readLine()) != null) {
			idx = 0;
			idxStart = 0;
			idxEnd = 0;
			int i = 0;
			while (idx < sCurrentLine.length() && i < headers.size()) {
				idxStart = idx;
				idxEnd = sCurrentLine.indexOf("\t", idx);
				if (idxEnd < 0)
					idxEnd = sCurrentLine.length();
				item = sCurrentLine.substring(idxStart, idxEnd);
				if (i >= 1) {
					SortedMap featureMap = (SortedMap) mappingRuleMap
							.get(headers.get(i));
					if (featureMap == null || featureMap.isEmpty()) {
						if (saveAll) {
							bw.write(item + "\t");
						}
					} else {
						if (!(item == null || item.equals(""))) {
							String value = "";
							ArrayList curValues = new ArrayList();
							if (item.indexOf("|") > 0) {
								int end = 0;
								String itemleft = item;
								while (itemleft.indexOf("|") > 0) {
									end = itemleft.indexOf("|");
									String curItem = itemleft.substring(0, end);
									// if (value.length() == 0) {
									value = String.valueOf(featureMap
											.get(curItem));
									if ((!value.equals("null"))
											&& (!value.isEmpty()))
										if (!curValues.contains(value))
											curValues.add(value);
									/*
									 * } else { value = value + "," +
									 * String.valueOf(featureMap.get(curItem));
									 * }
									 */
									itemleft = itemleft.substring(end + 1);
								}
								// value = value + "," +
								// String.valueOf(featureMap.get(itemleft));
								value = String
										.valueOf(featureMap.get(itemleft));
								if ((!value.equals("null"))
										&& (!value.isEmpty()))
									if (!curValues.contains(value))
										curValues.add(value);
							} else {
								value = String.valueOf(featureMap.get(item));
								if ((!value.equals("null"))
										&& (!value.isEmpty()))
									if (!curValues.contains(value))
										curValues.add(value);
							}
							value = "";
							for (int l = 0; l < curValues.size(); l++) {
								value += curValues.get(l) + ",";
							}
							if ((!value.equals("null")) && (!value.isEmpty())) {
								value = value.substring(0, value.length() - 1);
								bw.write(value + "\t");
							} else {
								bw.write("\t");
							}
						} else {
							bw.write("" + "\t");
						}
					}
				} else {
					bw.write(item + "\t");
				}
				i++;
				idx = idxEnd + 1;
			}
			bw.write("\r\n");
			//	bw.newLine();// 换行
			bw.flush();
			// System.out.println(sCurrentLine);
		}
		bw.close();
		br.close();
	}

	public void saveNex(String filename, String outfilename,
			HashMap mappingRuleMap, HashMap binMappingRuleMap, boolean saveAll) throws IOException {
		BufferedWriter bw;
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> firstColumn = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();

		int columnNum = readContents(filename, mappingRuleMap, saveAll,
				headers, firstColumn, contents, values);

		bw = new BufferedWriter(new FileWriter(outfilename));

		bw.write("#NEXUS" + "\r\n");
		bw.write("BEGIN TAXA;	DIMENSIONS  NTAX="+ firstColumn.size() + ";\r\n");
		bw.write("TAXLABELS\r\n");
		for (int i = 0; i < firstColumn.size(); i++) {
			bw.write("       "+firstColumn.get(i));
			bw.write("\r\n");
			//	bw.newLine();// 换行
			bw.flush();
		}
		bw.write(";\r\nEND;\r\n\r\n");
		
		bw.write("BEGIN CHARACTERS;\r\n");		
		bw.write("     DIMENSIONS NCHAR="
				+ String.valueOf(columnNum) + ";");
		
/*		bw.write("BEGIN DATA;" + "\r\n\t");
		bw.write("DIMENSIONS NTAX=" + firstColumn.size() + " NCHAR="
				+ String.valueOf(columnNum) + ";");
		bw.newLine();// 换行*/
		bw.write("     FORMAT DATATYPE=Standard SYMBOLS= \"");
		for (int i = 0; i < values.size(); i++) {
			bw.write(values.get(i));
			if (i != values.size() - 1) {
				bw.write(" ");
			}
		}

		bw.write("\" MISSING=? GAP= -;");
		bw.write("\r\n");
	//	bw.newLine();
		
		saveCharacterPart(bw, mappingRuleMap,binMappingRuleMap, headers, saveAll);
		bw.write("\r\n");
	//	bw.newLine();
		bw.write("\r\n");
	//	bw.newLine();
		
		bw.write("MATRIX");
		bw.write("\r\n");
	//	bw.newLine();
		bw.flush();
		for (int i = 0; i < firstColumn.size(); i++) {
			bw.write(firstColumn.get(i) + "\t");
			bw.write(contents.get(i)+ "\r\n");
		//	bw.newLine();// 换行
			bw.flush();
		}
		bw.write(";");
		bw.write("\r\n");
	//	bw.newLine();
		bw.write("END;");
		bw.write("\r\n");
	//	bw.newLine();

	/*	bw.newLine();
		bw.newLine();
		bw.write("[");
		bw.newLine();
		saveCharacterPart(bw, mappingRuleMap,binMappingRuleMap, headers, saveAll);
		bw.newLine();
		bw.write("]");*/

		bw.flush();
		bw.close();

		// output character file
		// String outCharacterFile = outfilename.substring(0,
		// outfilename.lastIndexOf(".")) + "Character" + ".txt";
		// saveCharacterFile(outCharacterFile, mappingRuleMap, headers,
		// saveAll);

	}

	public void savePhy(String filename, String outfilename,
			HashMap mappingRuleMap, boolean saveAll) throws IOException {
		BufferedWriter bw;
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> firstColumn = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));

		int columnNum = readContents(filename, mappingRuleMap, saveAll,
				headers, firstColumn, contents, values);

		bw = new BufferedWriter(new FileWriter(outfilename));

		bw.write(firstColumn.size() + " " + columnNum);// headers.size()
		bw.newLine();// 换行
		bw.flush();
		for (int i = 0; i < firstColumn.size(); i++) {
			bw.write(firstColumn.get(i) + "\t");
			bw.write(contents.get(i));
			bw.write("\r\n");
			//	bw.newLine();// 换行
			bw.flush();
		}
		bw.close();
		br.close();
		// output character file
		String outCharacterFile = outfilename.substring(0,
				outfilename.lastIndexOf("."))
				+ "Character" + ".txt";
		saveCharacterFile(outCharacterFile, mappingRuleMap, headers, saveAll);
	}

	protected int readContents(String filename, HashMap mappingRuleMap,
			boolean saveAll, ArrayList<String> headers,
			ArrayList<String> firstColumn, ArrayList<String> contents,
			ArrayList<String> values) throws FileNotFoundException, IOException {

		BufferedReader br = new BufferedReader(new FileReader(filename));
		// read the header of table
		String sCurrentLine;
		sCurrentLine = br.readLine();
		int idx = 0;
		int idxStart = 0, idxEnd = 0;
		String item;
		while (idx < sCurrentLine.length()) {
			idxStart = idx;
			idxEnd = sCurrentLine.indexOf("\t", idx);
			if (idxEnd < 0)
				idxEnd = sCurrentLine.length();
			item = sCurrentLine.substring(idxStart, idxEnd);
			headers.add(item);
			idx = idxEnd + 1;
		}

		int columnNum = 0;
		// output table contents
		while ((sCurrentLine = br.readLine()) != null) {
			idx = 0;
			idxStart = 0;
			idxEnd = 0;
			int i = 0;
			columnNum = 0;
			String curRow = "";
			while (idx < sCurrentLine.length() && i < headers.size()) {
				idxStart = idx;
				idxEnd = sCurrentLine.indexOf("\t", idx);
				if (idxEnd < 0)
					idxEnd = sCurrentLine.length();
				item = sCurrentLine.substring(idxStart, idxEnd);
				if (StringPattern.isENum(item)) {
					BigDecimal db = new BigDecimal(item);
					item = db.toPlainString();
				}

				if (i >= 1) {
					SortedMap featureMap = (SortedMap) mappingRuleMap
							.get(headers.get(i));
					if (featureMap == null || featureMap.isEmpty()) {
						if (saveAll) {
							curRow = curRow + "?";
						}
					} else {
						columnNum++;
						if (!(item == null || item.equals(""))) {
							String value = "";
							ArrayList curValues = new ArrayList();
							if (item.indexOf("|") > 0) {
								int end = 0;
								String itemleft = item;
								while (itemleft.indexOf("|") > 0) {
									end = itemleft.indexOf("|");
									String curItem = itemleft.substring(0, end);
									// if (value.length() == 0) {
									value = String.valueOf(featureMap
											.get(curItem));
									if ((!value.equals("null"))
											&& (!value.isEmpty())) {									
										if (!values.contains(value)) {
											values.add(value);
										}
										if (!curValues.contains(value))
											curValues.add(value);
									}
									/*
									 * } else { if
									 * (!values.contains(String.valueOf
									 * (featureMap.get(curItem)))) {
									 * values.add((String)
									 * String.valueOf(featureMap.get(curItem)));
									 * } value = value + (String)
									 * String.valueOf(featureMap.get(curItem));
									 * if (!curValues.contains(value))
									 * curValues.add(value); }
									 */
									itemleft = itemleft.substring(end + 1);
								}
								value = String
										.valueOf(featureMap.get(itemleft));
								if ((!value.equals("null"))
										&& (!value.isEmpty())) {
									if (!values.contains(value)) {
										values.add(value);
									}
									if (!curValues.contains(value))
										curValues.add(value);
								}
								// value = "{" + value + (String)
								// String.valueOf(featureMap.get(itemleft)) +
								// "}";

							} else {
								value = String.valueOf(featureMap
										.get(item));
								if ((!value.equals("null"))
										&& (!value.isEmpty())) {
									if (!values.contains(value)) {
										values.add(value);
									}
									if (!curValues.contains(value))
										curValues.add(value);
								}
							}
							value = "";
							for (int l = 0; l < curValues.size(); l++) {
								value += curValues.get(l);
							}
							if (curValues.size() > 1)
								value = "{" + value + "}";
							curRow = curRow + value;

						} else {
							curRow = curRow + "?";
						}
					}
				} else {
					firstColumn.add(item);
					curRow = "";
				}
				i++;
				idx = idxEnd + 1;
			}
			contents.add(curRow);
		}
		br.close();

		if (saveAll)
			return headers.size();
		else
			return columnNum;
	}

	private void saveCharacterFile(String outCharacterFile,
			HashMap mappingRuleMap, ArrayList<String> headers, boolean saveAll)
			throws IOException {
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(outCharacterFile));
		bw.write("CHARSTATELABELS");
		bw.write("\r\n");
	//	bw.newLine();
		bw.flush();
		int columnNum = 0;
		for (int i = 1; i < headers.size(); i++) {
			SortedMap featureMap = (SortedMap) mappingRuleMap.get(headers
					.get(i));
			if (saveAll && (featureMap == null || featureMap.isEmpty())) {
				bw.write("\t\t");
				bw.write(String.valueOf(i));
				bw.write(" \'");
				bw.write(headers.get(i));
				bw.write(" \'");
				bw.write(" / ");
				if (!(featureMap == null || featureMap.isEmpty())) {
					Iterator it = featureMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pairs = (Map.Entry) it.next();
						bw.write(" \'");
						bw.write((String) pairs.getKey());
						bw.write("\' ");
						// bw.write(",");
					}
				}
				bw.write("\r\n");
				//	bw.newLine();
				bw.flush();
			} else if (!saveAll
					&& (!(featureMap == null || featureMap.isEmpty()))) {
				columnNum++;
				bw.write("\t\t");
				bw.write(String.valueOf(columnNum));
				bw.write(" \'");
				bw.write(headers.get(i));
				bw.write(" \'");
				bw.write(" / ");
				Iterator it = featureMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					bw.write(" \'");
					bw.write((String) pairs.getKey());
					bw.write("\' ");
					// bw.write(",");
				}
				bw.write("\r\n");
				//	bw.newLine();
				bw.flush();
			}
		}
		bw.flush();
		bw.close();
	}

	private void saveCharacterPart(BufferedWriter bw, HashMap mappingRuleMap,HashMap binMappingRuleMap, ArrayList<String> headers, boolean saveAll) throws IOException {
        bw.write("CHARSTATELABELS");
		bw.write("\r\n");
	//	bw.newLine();
        bw.flush();
        int columnNum = 0;
        for (int i = 1; i < headers.size(); i++) {
            SortedMap featureMap = (SortedMap) mappingRuleMap.get(headers.get(i));
            SortedMap featureBinMap = (SortedMap) binMappingRuleMap.get(headers.get(i));
            SortedMap keyToStatesMap = new TreeMap();
            if (!(featureMap == null || featureMap.isEmpty())) {
            	Iterator itState = featureMap.entrySet().iterator();
    			while (itState.hasNext()) {
    				Map.Entry pairs = (Map.Entry) itState.next();
    				String key = String.valueOf(pairs.getValue());
    				String state = (String) pairs.getKey();
    				if (keyToStatesMap.containsKey(key)){
    					String states = (String) keyToStatesMap.get(key);
    					keyToStatesMap.put(key, states+"_"+ state);
    				}else{
    					keyToStatesMap.put(key, state);
    				}
    			}
            }
            
            if (saveAll && (featureMap == null || featureMap.isEmpty())) {
                bw.write("\t\t");
                bw.write(String.valueOf(i)+" ");
          //      bw.write(" \'");
                bw.write(headers.get(i));
          //      bw.write(" \'");
                bw.write(" / ");
                if (!(featureMap == null || featureMap.isEmpty())) {
                    Iterator it;
    				if (featureBinMap == null) {
    			//		it = featureMap.entrySet().iterator();
    					it = keyToStatesMap.entrySet().iterator();
    				} else {
    					it = featureBinMap.entrySet().iterator();
    				}
//                    Iterator it = featureMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
               //         bw.write(" \'");
        				//	bw.write((String) pairs.getKey());
    					bw.write((String) pairs.getValue());
               //         bw.write("\' ");
         /*               bw.write(" : \'");
                        bw.write((String) pairs.getValue());
                        bw.write("\' ");*/
          //              bw.write(",");  
    					bw.write(" "); 
                    }
                }
                bw.write(",");
        		bw.write("\r\n");
        		//	bw.newLine();
                bw.flush();
            } else if (!saveAll && (!(featureMap == null || featureMap.isEmpty()))) {
                columnNum++;
                bw.write("\t\t");
                bw.write(String.valueOf(columnNum)+" ");
          //      bw.write(" \'");
                bw.write(headers.get(i));
         //       bw.write(" \'");
                bw.write(" / ");
                
                Iterator it;
				if (featureBinMap == null) {
	    			//		it = featureMap.entrySet().iterator();
					it = keyToStatesMap.entrySet().iterator();		
				} else {
					it = featureBinMap.entrySet().iterator();
				}
				
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
				//	bw.write(" \'");
				//	bw.write((String) pairs.getKey());
					bw.write((String) pairs.getValue());
				//	bw.write("\' ");
					/*
					 * bw.write(" : \'"); bw.write((String)
					 * pairs.getValue()); bw.write("\' ");
					 */
			//		bw.write(",");
					bw.write(" ");
				}
                bw.write(",");
        		bw.write("\r\n");
        		//	bw.newLine();
                bw.flush();
            }
        }
        bw.write(";");
        bw.flush();
    }


}
