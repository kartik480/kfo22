package com.kfinone.app;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataService {
    private static final String TAG = "DataService";

    public interface DataCallback {
        void onSuccess(int employeeCount, int sdsaCount, int partnerCount, int portfolioCount, int agentCount);
        void onError(String error);
    }

    public static void fetchDashboardData(DataCallback callback) {
        new Thread(() -> {
            try {
                // Fetch employee count
                int employeeCount = fetchEmployeeCount();
                Log.d(TAG, "Employee count: " + employeeCount);

                // Fetch SDSA count
                int sdsaCount = fetchSdsaCount();
                Log.d(TAG, "SDSA count: " + sdsaCount);

                // Fetch partner count (placeholder for now)
                int partnerCount = fetchPartnerCount();
                Log.d(TAG, "Partner count: " + partnerCount);

                // Fetch portfolio count (placeholder for now)
                int portfolioCount = fetchPortfolioCount();
                Log.d(TAG, "Portfolio count: " + portfolioCount);

                // Fetch agent count (placeholder for now)
                int agentCount = fetchAgentCount();
                Log.d(TAG, "Agent count: " + agentCount);

                // Return results
                callback.onSuccess(employeeCount, sdsaCount, partnerCount, portfolioCount, agentCount);

            } catch (Exception e) {
                Log.e(TAG, "Error fetching dashboard data: " + e.getMessage(), e);
                callback.onError("Error fetching data: " + e.getMessage());
            }
        }).start();
    }

    private static int fetchEmployeeCount() {
        try {
            URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_emp_panel_users.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                if (json.getString("status").equals("success")) {
                    JSONArray data = json.getJSONArray("data");
                    return data.length();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching employee count: " + e.getMessage(), e);
        }
        return 0;
    }

    private static int fetchSdsaCount() {
        try {
            URL url = new URL("https://emp.kfinone.com/mobile/api/get_my_sdsa_users.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                if (json.getString("status").equals("success")) {
                    // Use the count field from the API response
                    int count = json.optInt("count", 0);
                    Log.d(TAG, "My SDSA users count: " + count);
                    return count;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching My SDSA count: " + e.getMessage(), e);
        }
        return 0;
    }

    private static int fetchPartnerCount() {
        // TODO: Implement actual partner count API call
        // For now, return a placeholder value
        return 0;
    }

    private static int fetchPortfolioCount() {
        // TODO: Implement actual portfolio count API call
        // For now, return a placeholder value
        return 0;
    }

    private static int fetchAgentCount() {
        // TODO: Implement actual agent count API call
        // For now, return a placeholder value
        return 0;
    }
} 