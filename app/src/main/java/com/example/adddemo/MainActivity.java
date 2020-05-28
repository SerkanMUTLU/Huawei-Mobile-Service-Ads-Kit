package com.example.adddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private EditText edtFindNumber;
    private Button btnFindNumber,btnWatchAdds;
    private TextView txtAllChance;
    private int totalChance=3;
    private Random rndNumber;
    private int randomNumber;
    private int maxNumber=100;
    private RewardAd rewardAd;

    private String adID ="testw6vs28auh3";
    private ConstraintLayout constLytBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllComponents();

        randomNumber = generateRandomNumber();
        showAllChance();
        Toast.makeText(getApplicationContext(),"Random Number is "+randomNumber,Toast.LENGTH_LONG).show();
        HwAds.init(this);

        //Set Bottom Banner
        setBannerAd();

        loadRewardAd();

    }

    private void setBannerAd(){
        BannerView bottomBanner = new BannerView(this);
        AdParam adParam = new AdParam.Builder().build();
        bottomBanner.setAdId(adID);
        bottomBanner.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
        bottomBanner.loadAd(adParam);
        constLytBanner.addView(bottomBanner);
    }


    public void setTotalChance(int chance){
        this.totalChance = chance;
        if(this.totalChance == 0){
            btnFindNumber.setEnabled(false);
            btnWatchAdds.setVisibility(View.VISIBLE);
        }
        else{
            btnFindNumber.setEnabled(true);
            btnWatchAdds.setVisibility(View.INVISIBLE);
        }
    }

    private void loadRewardAd(){
        if(rewardAd == null){
            rewardAd = new RewardAd(MainActivity.this,getString(R.string.reward_ad_id));
        }

        RewardAdLoadListener rewardAdLoadListener = new RewardAdLoadListener(){
            @Override
            public void onRewardAdFailedToLoad(int i) {
                //i returns error code
                Log.e("ERROR",""+i);
            }

            @Override
            public void onRewardedLoaded() {
                Log.i("INFO","Your reward was added successfully");
            }
        };

        rewardAd.loadAd(new AdParam.Builder().build(),rewardAdLoadListener);
    }

    private void rewardAdShow(){
        if(rewardAd.isLoaded()){
            rewardAd.show(MainActivity.this, new RewardAdStatusListener() {

                @Override
                public void onRewardAdClosed() {
                    loadRewardAd();
                }

                @Override
                public void onRewardAdFailedToShow(int i) {
                    Log.e("ERROR",String.valueOf(i));
                }

                @Override
                public void onRewardAdOpened() {
                    Toast.makeText(MainActivity.this,"onRewardAdOpened",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRewarded(Reward reward) {
                    //If you want to give reward to player , you can declare reward
                    reward = new Reward() {
                        @Override
                        public String getName() {
                            return "Extra playing Change ";//My reword name
                        }

                        @Override
                        public int getAmount() { //specify reward amount
                            return 1;
                        }
                    };

                    totalChance+=reward.getAmount();

                    Toast.makeText(getApplicationContext(),reward.getName()+"+"+reward.getAmount(),Toast.LENGTH_SHORT).show();
                    setTotalChance(totalChance);
                    showAllChance();

                    loadRewardAd();
                }
            });
        }
    }

    private int generateRandomNumber(){
        return rndNumber.nextInt(maxNumber);
    }

    private void showAllChance(){
        txtAllChance.setText(String.format("Total Chance : %d",totalChance));
    }

    private boolean isThereAnyChance(){
        if(totalChance == 0){
            return false;
        }
        else{
            return true;
        }
    }

    private void checkNumber(){

        if(isThereAnyChance()){
            int enteredNumber = Integer.parseInt(edtFindNumber.getText().toString().isEmpty()?"-1":edtFindNumber.getText().toString());

            //Check responsed number
            if(enteredNumber == -1){
                Toast.makeText(getApplicationContext(),"Enter a number !",Toast.LENGTH_LONG).show();
            }
            else{
                //Tutlan sayı ve tahmin eşitse yeni bir sayı üret
                if(String.valueOf(randomNumber).equals(String.valueOf(enteredNumber))){
                    Toast.makeText(getApplicationContext(),"Doğru",Toast.LENGTH_SHORT).show();
                    showAllChance();

                    randomNumber=generateRandomNumber();//şifre doğruysa yeni numara üret
                    Toast.makeText(getApplicationContext(),"New random number"+randomNumber,Toast.LENGTH_SHORT).show();
                }
                else{//Eşit değilse candan 1 düşür

                    this.totalChance--;
                    setTotalChance(totalChance);
                    showAllChance();

                    Toast.makeText(getApplicationContext(),"Yanlış sayi ipucu("+randomNumber+")",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setAllComponents(){
        edtFindNumber=findViewById(R.id.edtNmbrFindNumber);

        btnWatchAdds=findViewById(R.id.btnWatchAdd);
        btnWatchAdds.setOnClickListener(this);

        btnFindNumber=findViewById(R.id.btnFindNumber);
        btnFindNumber.setOnClickListener(this);

        txtAllChance=findViewById(R.id.txtVwchance);
        rndNumber = new Random();

        constLytBanner = findViewById(R.id.consLytBanner);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFindNumber:
                checkNumber();
                edtFindNumber.setText("");
                break;
            case R.id.btnWatchAdd:
                rewardAdShow();//triggered reward listeners
                break;
            default:
                break;
        }
    }
}
