package ch.heigvd.api.labio.impl;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *       *** IMPORTANT WARNING : DO NOT EDIT THIS FILE ***
 *
 * This file is used to specify what you have to implement. To check your work,
 * we will run our own copy of the automated tests. If you change this file,
 * then you will introduce a change of specification!!!
 *
 * @author Juergen Ehrensberger, Miguel Santamaria
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileTransformerTest {
    final String workingDirectory = "./workspace/testFileTransformer";

    private void createTestFile(String path, String filename, String content)
            throws IOException {
        Path filepath = Paths.get(path, filename);
        filepath.getParent().toFile().mkdirs();
        Writer writer = new OutputStreamWriter(new FileOutputStream(filepath.toFile()), "UTF-8");
        writer.write(content);
        writer.close();
    }

    @BeforeAll
    @AfterAll
    public void clearFiles() throws IOException {
        FileUtils.deleteDirectory(new File(workingDirectory));
    }

    @Test
    public void itShouldCreateAnOutFile() throws IOException {
        // Create a test file
        String path = Paths.get(workingDirectory, "firstLevel", "secondLevel").toString();
        String filename = "testfile.utf8";

        String content = "ABC";
        createTestFile(path, filename, content);

        // Check if the FileTransformer creates a new file
        File inputFile = Paths.get(path, filename).toFile();
        File outputFile = Paths.get(path, filename + ".out").toFile();

        FileTransformer fileTransformer = new FileTransformer();
        fileTransformer.transform(inputFile);

        assertTrue(inputFile.exists() && inputFile.isFile());
        assertTrue(outputFile.exists() && outputFile.isFile());
    }

    @Test
    public void itShouldHandleUnicodeCharacters() throws IOException {
        // Create a test file
        String path = Paths.get(workingDirectory, "firstLevel", "otherLevel").toString();
        String filename = "unicode.utf8";

        String content = "ABCD ÄÖÜ ÀÉÙÇ 北京 ☺";
        createTestFile(path, filename, content);

        // Check if the FileTransformer creates a new file
        File inputFile = Paths.get(path, filename).toFile();
        File outputFile = Paths.get(path, filename + ".out").toFile();

        FileTransformer fileTransformer = new FileTransformer();
        fileTransformer.transform(inputFile);

        // Check content of output file
        char[] buffer = new char[255];
        Reader reader = new InputStreamReader(new FileInputStream(outputFile), "UTF-8");
        int l = reader.read(buffer);
        reader.close();
        String output = String.valueOf(buffer, 0, l);
        assertTrue(content.equals(output) || ("1. " + content).equals(output));
    }

    @Test
    public void itShouldApplyTheTwoTransformations() throws IOException {
        // Create a test file
        String path = Paths.get(workingDirectory, "firstLevel", "otherLevel").toString();
        String filename = "testtransformations.utf8";

        String source = "abcdefgABCDEFG\t12345 !?'.\r\nAnother Line...\nThird Line.\r\n";
        String target = "1. ABCDEFGABCDEFG\t12345 !?'.\n2. ANOTHER LINE...\n3. THIRD LINE.\n4. ";

        createTestFile(path, filename, source);

        // Check if the FileTransformer creates a new file
        File inputFile = Paths.get(path, filename).toFile();
        File outputFile = Paths.get(path, filename + ".out").toFile();

        FileTransformer fileTransformer = new FileTransformer();
        fileTransformer.transform(inputFile);

        // Check content of output file
        char[] buffer = new char[255];
        Reader reader = new InputStreamReader(new FileInputStream(outputFile), "UTF-8");
        int l = reader.read(buffer);
        reader.close();
        String output = String.valueOf(buffer, 0, l);

        assertTrue(target.equals(output));
    }
}