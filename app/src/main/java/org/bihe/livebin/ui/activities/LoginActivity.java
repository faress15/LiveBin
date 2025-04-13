package org.bihe.livebin.ui.activities;


import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_NAME;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PASSWORD;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PERFORMANCE;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_USER_ID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.db.runnables.User.InsertUserRunnable;
import org.bihe.livebin.data.model.Admin;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.AdminServiceImpl;
import org.bihe.livebin.data.network.impl.UserServiceImpl;
import org.bihe.livebin.data.prefrences.PreferencesManager;
import org.bihe.livebin.databinding.ActivityLoginBinding;
import org.bihe.livebin.utils.DialogHelper;
import org.bihe.livebin.utils.ResultListener;
import org.bihe.livebin.utils.SyncService;
import org.bihe.livebin.utils.Validator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferencesManager preferencesManager;
    private ProgressDialog dialog;
    private ExecutorService executorService;

    private UserServiceImpl userService;

    private AdminServiceImpl adminService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
    }

    private void setListeners() {
        binding.signUpBtn.setOnClickListener(v -> onSignUpClicked());
        binding.signInBtn.setOnClickListener(v -> onLoginClicked());
    }

    private void onSignUpClicked() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void init() {
        executorService = Executors.newCachedThreadPool();
        userService = new UserServiceImpl();
        preferencesManager = PreferencesManager.getInstance(this);
        dialog = DialogHelper.getLoadingDialog(this);
        adminService = new AdminServiceImpl();
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void removeErrors() {
        binding.usernameEt.setError(null);
        binding.passwordEdt.setError(null);
    }

    private void onLoginClicked() {
        removeErrors();

        String username = binding.usernameEt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

        if (username.equals("admin") && password.equals("admin")) {
            showDialog();
            adminService.loginAdmin(new ResultListener<Admin>() {
                @Override
                public void onResult(Admin admin) {
                    dialog.dismiss();
                    showToastMessage(getString(R.string.login_success));
                    adminLogin();
                }

                @Override
                public void onError(Throwable throwable) {
                    dialog.dismiss();
                    showToastMessage(throwable.getMessage());
                }
            });
        } else {
            if (isInputValid(password)) {
                showDialog();
                User inputUser = new User(username, password);
                userService.loginUser(inputUser, new ResultListener<User>() {
                    @Override
                    public void onResult(User user) {
                        dialog.dismiss();
                        insertUserToDb(user);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dialog.dismiss();
                        showToastMessage(throwable.getMessage());
                    }
                });
            }
        }
    }

    private void insertUserToDb(User user) {
        showDialog();
        executorService.execute(
                new InsertUserRunnable(getApplicationContext(), user, new DbResponse<User>() {
                    @Override
                    public void onSuccess(User user) {
                        runOnUiThread(() -> {
                            showToastMessage(getString(R.string.login_success));
                            userLogin(user);
                        });
                    }

                    @Override
                    public void onError(Error error) {
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_login),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }));
    }

    private void userLogin(User user) {
        dialog.dismiss();
        AppData.getInstance().setCurrentUser(user);
        preferencesManager.put(PREF_KEY_IS_LOGIN, true);
        preferencesManager.put(PREF_KEY_USER_ID, user.getId());
        preferencesManager.put(PREF_KEY_PASSWORD, user.getPassword());
        preferencesManager.put(PREF_KEY_NAME, user.getUsername());
        preferencesManager.put(PREF_KEY_PERFORMANCE, user.getPerformance());

        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void adminLogin() {
        preferencesManager.put(PREF_KEY_IS_LOGIN, true);
        preferencesManager.put(PREF_KEY_PASSWORD, "admin");
        preferencesManager.put(PREF_KEY_NAME, "admin");
        preferencesManager.put(PREF_KEY_PERFORMANCE, getString(R.string.admin));

        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isInputValid(String password) {
        boolean isValid = true;

        if (!Validator.isPasswordValid(password)) {
            binding.passwordEdt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        return isValid;
    }

    private void showDialog() {
        String dialogTitle = getString(R.string.title_dialog);
        String dialogMessage = getString(R.string.title_dialog_message);
        dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}