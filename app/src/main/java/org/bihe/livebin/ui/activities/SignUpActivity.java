package org.bihe.livebin.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.bihe.livebin.R;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.UserServiceImpl;
import org.bihe.livebin.databinding.ActivitySignUpBinding;
import org.bihe.livebin.utils.DialogHelper;
import org.bihe.livebin.utils.ResultListener;
import org.bihe.livebin.utils.Validator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private User user;
    private ExecutorService executorService;

    private UserServiceImpl userService;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setUpToolbar();
        setListeners();



        binding.scanIdentificationCodeBtn.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(SignUpActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setCameraId(0);
            integrator.setPrompt(String.valueOf(R.string.scan_your_card));
            integrator.initiateScan();
        });
    }

    private void init() {
        dialog = DialogHelper.getLoadingDialog(this);
        executorService = Executors.newCachedThreadPool();
        userService = new UserServiceImpl();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
            } else {
                binding.identificationCodeEt.setText(result.getContents());
                Toast.makeText(this, R.string.barcode + result.getContents(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back);
        actionBar.setHomeAsUpIndicator(backDrawable);
    }

    private void setListeners() {
        binding.signUpBtn.setOnClickListener(v -> {
            signUpUser();
        });
    }


    private void signUpUser() {
        removeErrors();
        try {
            user = createUserObjectFromInputs();
            if (isUserValid()) {
                showDialog();
                userService.signupUser(user, new ResultListener<User>() {
                    @Override
                    public void onResult(User user) {
                        showToastMessage(getString(R.string.user_signup_success));
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dialog.dismiss();
                        showToastMessage("fuckkkk");
                    }
                });
            }
        } catch (Exception e) {
            showToastMessage(getString(R.string.error_invalid_input));
        }
    }


    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void removeErrors() {
        binding.nameEt.setError(null);
        binding.yearOfBirthEd.setError(null);
        binding.passwordEt.setError(null);
        binding.confirmPasswordEt.setError(null);
        binding.identificationCodeEt.setError(null);

    }

    private User createUserObjectFromInputs() {
        String performance = binding.spinnerPerformance.getSelectedItem().toString().trim();
        String username = binding.nameEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();
        int dayOfBirth = Integer.parseInt(binding.dayOfBirthEd.getText().toString().trim());
        int monthOfBirth = Integer.parseInt(binding.monthOfBirthEd.getText().toString().trim());
        int yearOfBirth = Integer.parseInt(binding.yearOfBirthEd.getText().toString().trim());
        String identificationCode = binding.identificationCodeEt.getText().toString().trim();
        return new User(username, password, identificationCode, performance, dayOfBirth, monthOfBirth, yearOfBirth);
    }


    private boolean isUserValid() {
        boolean isValid = true;

        if (TextUtils.isEmpty(user.getUsername())) {
            binding.nameEt.setError(getString(R.string.error_invalid_first_name));
            isValid = false;
        }

        if (!Validator.isPasswordValid(user.getPassword())) {
            binding.passwordEt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        String confirmPassword = binding.confirmPasswordEt.getText().toString().trim();
        if (!Validator.isPasswordValid(confirmPassword)) {
            binding.confirmPasswordEt.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        if (!user.getPassword().equals(confirmPassword)) {
            binding.confirmPasswordEt.setError(getString(R.string.error_unique_password));
            isValid = false;
        }

        if (!Validator.isIdentificationCodeValid(user.getIdentificationCode())) {
            binding.identificationCodeEt.setError(getString(R.string.error_wrong_identification_code));
            isValid = false;
        }
//        if (!checkNationalCode()[0]) {
//            binding.identificationCodeEt.setError(getString(R.string.user_already_exist));
//            isValid = false;
//        }
        if (calculateAge(user.getYearOfBirth(), user.getMonthOfBirth(), user.getDayOfBirth()) < 18 || calculateAge(user.getYearOfBirth(), user.getMonthOfBirth(), user.getDayOfBirth()) > 130) {
            binding.yearOfBirthEd.setError(getString(R.string.error_invalid_age));
            isValid = false;
        }
        if ((user.getMonthOfBirth() > 13 || user.getMonthOfBirth() < 0) || (user.getDayOfBirth() > 31 || user.getDayOfBirth() < 0)) {
            binding.monthOfBirthEd.setError(getString(R.string.error_invalid_month));
            binding.dayOfBirthEd.setError(getString(R.string.error_invalid_day));
            isValid = false;
        }
        if ((user.getMonthOfBirth() < 13 || user.getMonthOfBirth() > 6) && user.getDayOfBirth() == 31) {
            binding.dayOfBirthEd.setError(getString(R.string.error_invalid_dayOfMonth));
            isValid = false;
        }

        return isValid;
    }

//    private boolean[] checkNationalCode() {
//        final boolean[] isValid = {true};
//        String nationalCode = binding.identificationCodeEt.getText().toString().trim();
//        userService.checkNationalCode(nationalCode, new ResultListener<User>() {
//            @Override
//            public void onResult(User user) {
//                if (user != null){
//                    isValid[0] = false;
//                }
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                showToastMessage(throwable.getMessage());
//            }
//        });
//        return isValid;
//    }


    private int calculateAge(int birthYear, int birthMonth, int birthDay) {
        int currentYear = 1403;
        int currentMonth = 11;
        int currentDay = 26;

        int age = currentYear - birthYear;
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age--;
        }
        return age;
    }

    private void showDialog() {
        String dialogTitle = getString(R.string.title_dialog);
        String dialogMessage = getString(R.string.title_dialog_message);
        dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        executorService.shutdown();
    }

}
