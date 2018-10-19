package dataset;

import org.apache.http.*;
import com.google.gson.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaReposDetecter {

    public static final int NUMBER_OF_DOWNLOAD_REPOSITORIES = 10;
    private static final int RESULTS_PER_PAGE = 20;
    private static final int NUMBER_OF_PAGES = (int) Math.ceil((float) NUMBER_OF_DOWNLOAD_REPOSITORIES / RESULTS_PER_PAGE);

    public static final String REPOS_STORING_FILE = "java_repos.txt";
    public static final String LANGUAGE = "Java";
    public static final String SORTING = "stars";

    String query;
    File reposFullNamesFile;

    JavaReposDetecter() {
        reposFullNamesFile = new File(REPOS_STORING_FILE);
    }

    private void generateRepoNamesFile() throws Exception {
        for (int i = 0; i < NUMBER_OF_PAGES; i++) {
            getNamesAndStoreInFile(i);
        }
    }

    private void getNamesAndStoreInFile(int pageIndex) throws Exception {
        query = "https://api.github.com/search/repositories?page=" + pageIndex +
                "&per_page=" + RESULTS_PER_PAGE +
                "&q=language:" + LANGUAGE +
                "&sort=" + SORTING;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(query);
        request.addHeader("content-type", "application/json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(reposFullNamesFile));

        try {
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            System.out.println(json);

            JsonElement jelement = new JsonParser().parse(json).getAsJsonObject().get("items");
            JsonArray jarr = jelement.getAsJsonArray();

            for (int i = 0; i < jarr.size(); i++) {
                JsonObject jo = (JsonObject) jarr.get(i);
                String fullName = jo.get("full_name").toString();
                fullName = fullName.substring(1, fullName.length() - 1);

                System.out.println(fullName);
                writer.write(fullName);
                writer.newLine();
            }

        } catch (IOException | NullPointerException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        } finally {
            writer.close();
        }
    }

    ArrayList<String> getRepoFullNames() throws Exception {

        ArrayList<String> fullNames = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(reposFullNamesFile));

        try {

            String fullName = reader.readLine();
            while (fullName != null) {
                fullNames.add(fullName);
                fullName = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        reader.close();
        return fullNames;
    }

    boolean isFileAlreadyExist() {
        return reposFullNamesFile.exists();
    }

    public ArrayList<String> getReposIfNotExist() throws Exception {
        if (!isFileAlreadyExist()) {
            generateRepoNamesFile();
        }
        return getRepoFullNames();
    }
}
