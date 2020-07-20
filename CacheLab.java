import java.util.Scanner;                     
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

                                           
class cache {

	cacheSet[] cacheSet;
	String id;

	// Constructor.
	public cache(cacheSet[] set) {
		this.cacheSet = set;

	}
}

class cacheSet {

	cachelines[] line;

	// Constructor.
	public cacheSet(cachelines[] line) {

		this.line = line;

	}

}

class cachelines {

	// Properties of the cachelines.
	int validBit;
	int tagBit;
	String data;
	int time;

	// Constructor.
	public cachelines(int validBit, int tagBit, String data) {

		this.validBit = validBit;
		this.tagBit = tagBit;
		this.data = data;

	}
}

public class CacheLab {

	// Global variables for caches.
	static int L1s, L1E, L1b;
	static int L2s, L2E, L2b;
	static int L1S, L1B, L2S, L2B;
	static String filename = null;

	// Hit, miss, eviction values for caches.
	static int L1I_hit = 0;
	static int L1I_miss = 0;
	static int L1I_eviction = 0;
	static int L1D_hit = 0, L1D_miss = 0, L1D_eviction = 0;
	static int L2_hit = 0, L2_miss = 0, L2_eviction = 0;

	// System time initially 0.
	static int SystemTime = 0;
	static int tempSystemTime = 0;

	public static void main(String[] args) throws Exception {

		// Take command-line arguments.
		getCommandLineArguments(args);

		// Cachelines for caches.
		final cachelines[] lineL1I = new cachelines[L1E];
		final cachelines[] lineL1D = new cachelines[L1E];
		final cachelines[] lineL2 = new cachelines[L2E];

		// Fill the lines with necessary info.
		for (int i = 0; i < L1E; i++) {

			// Set the parameters by default.
			lineL1I[i] = new cachelines(1, 0, null);
			lineL1D[i] = new cachelines(1, 0, null);
		}

		// Same procedure for L2 cache.
		for (int i = 0; i < L2E; i++) {

			// Set the parameters by default.
			lineL2[i] = new cachelines(1, 0, null);
		}

		// CacheSet for caches.
		final cacheSet[] setL1I = new cacheSet[L1S];
		final cacheSet[] setL1D = new cacheSet[L1S];
		final cacheSet[] setL2 = new cacheSet[L2S];

		// Fill the sets with cache lines.
		for (int i = 0; i < L1S; i++) {

			// Create cacheSet with given cachline.
			setL1I[i] = new cacheSet(lineL1I);
			setL1D[i] = new cacheSet(lineL1D);

		}

		// Same procedure for L2 cache.
		for (int i = 0; i < L2S; i++) {

			// Create cacheSet with given cacheline.
			setL2[i] = new cacheSet(lineL2);

		}

		final cache L1I = new cache(setL1I); // L1I Cache.
		final cache L1D = new cache(setL1D); // L1D Cache.
		final cache L2 = new cache(setL2); // L2 Cache.

		// Set ID values.
		L1I.id = "L1I";
		L1D.id = "L1D";
		L2.id = "L2";

		readFile(L1I, L1D, L2); // Read the trace file

		// Print Results.
		printHitMissEviction(L1I, L1I_hit, L1I_miss, L1I_eviction); // L1I Results.
		printHitMissEviction(L1D, L1D_hit, L1D_miss, L1D_eviction); // L1D Results.
		printHitMissEviction(L2, L2_hit, L2_miss, L2_eviction); // L2 Results.

	}

	// Print the final results for L1I, L1D, and L2 cache.
	public static void printHitMissEviction(cache cache, int hit, int miss, int eviction) {

		System.out.println();
		System.out.println(cache.id + " hits: " + hit + " misses: " + miss + " evictions: " + eviction);
	}

	// Get parameters from command-line.
	public static void getCommandLineArguments(String[] args) {

		if (args.length > 1) {

			try {
				L1s = Integer.parseInt(args[1]); // L1s
				L1E = Integer.parseInt(args[3]); // L1E
				L1b = Integer.parseInt(args[5]); // L1b
				L2s = Integer.parseInt(args[7]); // L2s
				L2E = Integer.parseInt(args[9]); // L2E
				L2b = Integer.parseInt(args[11]); // L2b
				filename = args[13]; // filename

			} catch (Exception e) {

				System.err.println("Argument not found.");
				System.exit(1);
			}
		}

		L1S = (int) Math.pow(2, L1s); // L1S value.
		L1B = (int) Math.pow(2, L1b); // L1B value.
		L2S = (int) Math.pow(2, L2s); // L2S value.
		L2B = (int) Math.pow(2, L2b); // L2B value.
	}

	// Read the .trace files with given order.
	public static void readFile(cache L1I, cache L1D, cache L2) throws Exception {

		Scanner scanner = new Scanner(new File(filename));
		String address;
		String data;
		int size;
		while (scanner.hasNextLine()) {

			String line = scanner.nextLine(); // Line-by-line read the file.
			String OperationTag = line.substring(0, line.indexOf(" ")); // Take Operation Tag ( I, L, M, S)

			switch (OperationTag) {

			case "I": { // [I] instruction section.

				// Take address and size values from the line.
				address = line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" ") - 1);
				size = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));

				// size --> 0 skip to the next instruction.
				if (size == 0) {

					continue;
				}
				// Instruction, address and size info.
				System.out.println("I " + address + " " + size);

				// Execute the instruction with L1I cache.
				getInstructionAndData(L1I, L1D, L2, address, size, 1);

				break;
			}

			case "L": { // [L] instruction section.

				// Take address and size values from the line.
				address = line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" ") - 1);
				size = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));

				// size --> 0 skip to the next instruction.
				if (size == 0) {

					continue;
				}
				// Instruction, address and size info.
				System.out.println("L " + address + " " + size);

				// Execute the instruction with L1D cache.
				getInstructionAndData(L1I, L1D, L2, address, size, 0);

				break;
			}

			case "M": { // [M] instruction section.

				// Take address and size values from the line.
				address = line.substring(line.indexOf(" ") + 1, line.indexOf(","));
				size = Integer.parseInt(line.substring(line.indexOf(",") + 2, line.lastIndexOf(" ") - 1));

				// size --> 0 skip to the next instruction.
				if (size == 0) {

					continue;
				}

				// Take the data value from the line.
				data = line.substring(line.lastIndexOf(" ") + 1, line.length());

				// Instruction, address, size and data info.
				System.out.println("M " + address + " " + size + " " + data);

				// Modify the given data for L1I, L1D and L2 cache.
				ModifyData(L1I, L1D, L2, size, address, data);

				break;

			}

			case "S": { // [S] instruction section.

				// Take address and size values from the line.
				address = line.substring(line.indexOf(" ") + 1, line.indexOf(","));
				size = Integer.parseInt(line.substring(line.indexOf(",") + 2, line.lastIndexOf(" ") - 1));

				// size --> 0 skip to the next instruction.
				if (size == 0) {

					continue;
				}

				// Take the data value from the line.
				data = line.substring(line.lastIndexOf(" ") + 1, line.length());

				// Instruction, address, size and data info.
				System.out.println("S " + address + " " + size + " " + data);

				// Store the given data for L1D and L2 cache.
				StoreData(L1D, L2, address, size, data);

				break;
			}

			default:
				throw new IllegalArgumentException("Unexpected value: " + OperationTag);
			}
		}
	}

	// Mainly used for [I] and [L] instructions with L1D and L1I caches.
	public static void getInstructionAndData(cache L1I, cache L1D, cache L2, String address, int size, int cacheType)
			throws IOException {

		cache currentCache = null;

		// Indicate the cache type for L1 caches for instructions. L1I -> 1 | L1D -> 0
		if (cacheType == 1)
			currentCache = L1I;
		else
			currentCache = L1D;

		// Convert the address hexadecimal to decimal.
		int decimalAddress = Integer.parseInt(address, 16);

		// Getting cache set, tag, and block size information for L1I, L1D and L2 cache.

		int L1TagSize = 32 - L1s - L1b;
		int L1SetBits = (decimalAddress << L1TagSize) >> (32 - L1s);
		int L1TagBits =  decimalAddress >> (32 - L1TagSize);

		
		int L2TagSize = 32 - L2s - L2b;
		int L2SetBits = (decimalAddress << L2TagSize) >> (32 - L2s);
		int L2TagBits = decimalAddress >> (32 - L2TagSize);

		// Firstly checking the caches lines for getting pre information for hit and
		// miss information.
		int L1PositionLine = checkInstruction(currentCache, L1E, L1SetBits, L1TagBits); // Returns valid line number.
		int L2PositionLine = checkInstruction(L2, L2E, L2SetBits, L2TagBits); // Returns valid line number.

		if (L1PositionLine != -1) { // Hit in L1.

			// Hit information.
			System.out.print(currentCache.id + " hit");

			if (cacheType == 1) { // Check Cache Type.

				L1I_hit++;

			} else {

				L1D_hit++;

			}

			// Update system time and L1 cache time.
			SystemTime++;
			tempSystemTime = SystemTime;
			currentCache.cacheSet[L1SetBits].line[L1PositionLine].time = tempSystemTime;

			if (L2PositionLine != -1) { // Hit in L2. Also hit in L1.

				// Hit information.
				System.out.print(", " + L2.id + " hit");
				System.out.println();

				L2_hit++;

				// Update system time and L2 cache time.
				SystemTime++;
				tempSystemTime = SystemTime;
				currentCache.cacheSet[L2SetBits].line[L2PositionLine].time = tempSystemTime;

			} else { // L1 -> Hit && L2 -> Miss | If miss, then eviction.

				// Miss information.
				System.out.print(", " + L2.id + " miss");
				System.out.println();
				L2_miss++;

				// Check eviction and write data to the cache.
				L2_eviction += checkEvictionAndWriteData(currentCache, L2, L1PositionLine, L1SetBits, L2SetBits,
						L2TagSize, L2E);
			}
		} else { // Miss in L1. Eviction for L1.

			// Miss information.
			System.out.print(currentCache.id + " miss");

			if (cacheType == 1) { // Check Cache Type.

				L1I_miss++;

			} else {

				L1D_miss++;

			}

			if (L2PositionLine != -1) { // Miss in L1 but hit in L2. Eviction for L1.

				// Hit information.
				System.out.print(", " + L2.id + " hit");
				System.out.println();
				L2_hit++;

				// Update System time and L2 cache time.
				SystemTime++;
				tempSystemTime = SystemTime;
				L2.cacheSet[L2SetBits].line[L2PositionLine].time = tempSystemTime;

				// Check
				// eviction.
				int L1Eviction = checkEvictionAndWriteData(L2, currentCache, L2PositionLine, L2SetBits, L1SetBits,
						L1TagBits, L1E);

				if (cacheType == 1) { // Check Cache Type.

					L1I_eviction = L1Eviction;

				} else {

					L1D_eviction = L1Eviction;

				}
			} else { // Miss in L1 and L2. We have to get the data from RAM.

				System.out.print(", " + L2.id + " miss");
				System.out.println();
				L2_miss++;

				// Fetch the ram and get the cache line.
				L2PositionLine = getLineNumberAndFetchRam(L2, L2SetBits, L2TagBits, decimalAddress);

				// Check eviction.
				int L1Eviction = checkEvictionAndWriteData(L2, currentCache, L2PositionLine, L2SetBits, L1SetBits,
						L1TagBits, L1E);

				if (cacheType == 1) { // Check Cache Type.

					L1I_eviction += L1Eviction;

				} else {

					L1D_eviction += L1Eviction;

				}
			}

		}
		System.out.println("   Place in " + L2.id + " set " + L2SetBits + "  " + currentCache.id + " set " + L1SetBits);
	}

	/*
	 * It checks the line is valid and also check tagBit and validBit. If valid line
	 * found return that line number.
	 */
	public static int checkInstruction(cache currentCache, int lineNumber, int CacheSetNumber, int TagBit) {

		for (int index = 0; index < lineNumber; index++) {

			if (currentCache.cacheSet[CacheSetNumber].line[index].validBit == 1
					&& currentCache.cacheSet[CacheSetNumber].line[index].tagBit == TagBit) {

				return index;
			}
		}
		return -1;
	}

	// Eviction control, If eviction occurs, then copy the instruction between
	// It checks the eviction and then write the data from current cache to
	// destination cache.
	public static int checkEvictionAndWriteData(cache currentCache, cache destinationCache, int currentCacheLineNumber,
			int currentCacheSetNumber, int destinationSetNumber, int cacheTag, int cacheLine) {

		int eviction = 0; // Eviction value.
		int positionLine = 0; // Id value during checking lines.
		boolean freeLine = false; // Free line for eviction checking.

		for (int index = 0; index < cacheLine; index++) {

			if (destinationCache.cacheSet[destinationSetNumber].line[index].validBit == 0) { // No Eviction.

				positionLine = index;
				freeLine = true; // If it is free then there is no eviction for caches.
				break;
			}
		}

		if (freeLine == false) { // There is eviction

			eviction++;
			SystemTime++;
			tempSystemTime = SystemTime;

			// Checking caches lines to copy process. (Using FIFO procedure).
			for (int index = 0; index < cacheLine; index++) {

				if (tempSystemTime > destinationCache.cacheSet[destinationSetNumber].line[index].time) {

					tempSystemTime = destinationCache.cacheSet[destinationSetNumber].line[index].time;
					positionLine = index;

				}

			}

		}

		// Copy process between caches.
		SystemTime++;
		tempSystemTime = SystemTime;
		destinationCache.cacheSet[destinationSetNumber].line[positionLine].validBit = 1;
		destinationCache.cacheSet[destinationSetNumber].line[positionLine].tagBit = cacheTag;
		destinationCache.cacheSet[destinationSetNumber].line[positionLine].time = tempSystemTime;
		String tempData = currentCache.cacheSet[currentCacheSetNumber].line[currentCacheLineNumber].data;
		destinationCache.cacheSet[destinationSetNumber].line[positionLine].data = tempData;

		return eviction;
	}

	// It reads the RAM from specified place and then stores data to the cache
	// finally returns line number which has stored data.
	public static int getLineNumberAndFetchRam(cache L2, int setNumber, int tagBit, int address) throws IOException {

		RandomAccessFile file = new RandomAccessFile("RAM.dat", "rw");
		file.seek(address); // Set Pointer to where the address yields in the RAM.
		byte[] data = new byte[3 * L2B - 1];
		file.read(data);
		file.close();
		String extractedData = new String(data); // Extracted Data from RAM.
		// System.out.println(extractedData);

		// Store the data to the appropriate line of L2.

		boolean freeLine = false;
		int positionLine = 0;

		for (int index = 0; index < L2E; index++) { // No Eviction.

			if (L2.cacheSet[setNumber].line[index].validBit == 0) {

				freeLine = true;
				positionLine = index;
				break;
			}
		}

		if (freeLine == false) { // Eviction here.

			L2_eviction++;

			// Update System time.
			SystemTime++;
			tempSystemTime = SystemTime;

			// Find appropriate line for cache to store the data.
			for (int index = 0; index < L2E; index++) {

				if (SystemTime > L2.cacheSet[setNumber].line[index].time) {

					positionLine = index;
					tempSystemTime = L2.cacheSet[setNumber].line[index].time;

				}
			}
		}

		// Write to L2 cache process.
		SystemTime++;
		tempSystemTime = SystemTime;
		L2.cacheSet[setNumber].line[positionLine].validBit = 1;
		L2.cacheSet[setNumber].line[positionLine].tagBit = tagBit;
		L2.cacheSet[setNumber].line[positionLine].time = tempSystemTime;
		L2.cacheSet[setNumber].line[positionLine].data = extractedData;

		return positionLine;
	}

	// It loads the data then stores it.
	public static void ModifyData(cache L1I, cache L1D, cache L2, int size, String address, String data)
			throws Exception {

		// Load the data and then store the data.
		getInstructionAndData(L1I, L1D, L2, address, size, 0);
		StoreData(L1D, L2, address, size, data);

	}

	// It takes the given data and stores it to the cache.
	public static void StoreData(cache L1D, cache L2, String address, int size, String data) throws Exception {

		int decimalAddress = Integer.parseInt(address, 16);

		// Getting cache set, tag, and block size information for L1I, L1D and L2
		// caches.

		int L1TagSize = 32 - L1s - L1b;
		int L1SetBits = (decimalAddress << L1TagSize) >> (32 - L1s);
		int L1TagBits =  decimalAddress >> (32 - L1TagSize);

		
		int L2TagSize = 32 - L2s - L2b;
		int L2SetBits = (decimalAddress << L2TagSize) >> (32 - L2s);
		int L2TagBits = decimalAddress >> (32 - L2TagSize);

		// Firstly checking the caches lines for getting pre information for hit and
		// miss information.

		int L1PositionLine = checkInstruction(L1D, L1E, L1SetBits, L1TagBits);
		int L2PositionLine = checkInstruction(L2, L2E, L2SetBits, L2TagBits);

		if (L1PositionLine != -1) { // Hit in L1D.

			System.out.print(L1D.id + " hit");
			L1D_hit++;

			// Update the system time and L1D cache time.
			SystemTime++;
			tempSystemTime = SystemTime;
			L1D.cacheSet[L1SetBits].line[L1PositionLine].time = tempSystemTime;

			// Update the L1D cache data.
			L1D.cacheSet[L1SetBits].line[L1PositionLine].data = data;

			if (L2PositionLine != -1) { // Hit in L2.

				System.out.print(", " + L2.id + " hit");
				System.out.println();
				L2_hit++;

				// Update the system time and L2 cache time.
				SystemTime++;
				tempSystemTime = SystemTime;
				L2.cacheSet[L2SetBits].line[L2PositionLine].time = tempSystemTime;

				// Update the L2 cache data.
				L2.cacheSet[L2SetBits].line[L2PositionLine].data = data;
			} else { // Hit in L1, Miss in L2. Eviction for L2.

				System.out.println(", " + L2.id + " miss");
				System.out.println();
				L2_miss++;

				L2_eviction += checkEvictionAndWriteData(L1D, L2, L1PositionLine, L1SetBits, L2SetBits, L2TagBits, L2E);
			}

		} else { // Miss in L1D. Eviction for L1D.

			System.out.print(L1D.id + " miss");
			L1D_miss++;

			if (L2PositionLine != -1) { // Miss in L1D, hit in L2.

				System.out.print(", " + L2.id + " hit");
				System.out.println();
				L2_hit++;

				// Take eviction for L1D and load the data to the L1D cache.
				L1D_eviction += checkEvictionAndWriteData(L2, L1D, L2PositionLine, L2SetBits, L1SetBits, L1TagBits, L1E);

			} else { // Miss L1 and L2 load the data from RAM.

				System.out.print(", " + L2.id + " miss");
				System.out.println();
				L2_miss++;

				// Store the data to L2 cache and get the appropriate line number..
				L2PositionLine = getLineNumberAndFetchRam(L2, L2SetBits, L1TagBits, decimalAddress);

				// Eviction for L1 caches.
				L1D_eviction = checkEvictionAndWriteData(L2, L1D, L2PositionLine, L2SetBits, L1SetBits, L1TagBits, L1E);
			}

			L1PositionLine = checkInstruction(L1D, L1E, L1SetBits, L1TagBits);

			// Update system time and L1D cache time.
			SystemTime++;
			tempSystemTime = SystemTime;
			L1D.cacheSet[L1SetBits].line[L1PositionLine].time = tempSystemTime;

			// Load the data to L1D cache.
			L1D.cacheSet[L1SetBits].line[L1PositionLine].data = data;

			@SuppressWarnings("unused") // This is only for write data.
			int storeData = checkEvictionAndWriteData(L1D, L2, L1PositionLine, L1SetBits, L2SetBits, L2TagBits, L2E);

		}
		
		// Get the data from L2 and write to the RAM.
		writeDatatoRAM(decimalAddress, size, data);

		System.out.println("    Store in " + L1D.id + ", " + L2.id + ", " + "RAM");

	}

	// Write the the given data to the specified place in the RAM.
	public static void writeDatatoRAM(int decimalAddress, int size, String data) throws Exception {

		RandomAccessFile file = new RandomAccessFile("RAM.dat", "rw"); // Random acces the RAM.dat

		file.seek(decimalAddress); // Set Pointer to where the address yields in the ram.
		file.write(data.getBytes()); // Convert the data in bytes then write to the specified place in the RAM.

		file.close();

	}

}