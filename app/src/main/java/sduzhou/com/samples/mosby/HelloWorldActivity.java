package sduzhou.com.samples.mosby;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sduzhou.com.samples.R;

public class HelloWorldActivity extends MvpActivity<HelloWorldView, HelloWorldPresenter>
                                implements HelloWorldView {

  @BindView(R.id.greetingTextView)
  TextView greetingTextView;

  @Override
  protected void onCreate(Bundle savedState){
    super.onCreate(savedState);
    setContentView(R.layout.activity_helloworld);
    ButterKnife.bind(this);
  }

  @Override // Called internally by Mosby
  public HelloWorldPresenter createPresenter(){
    return new HelloWorldPresenter();
  }

  @OnClick(R.id.helloButton)
  public void onHelloButtonClicked(){
    presenter.greetHello();
  }

  @OnClick(R.id.goodbyeButtonClicked)
  public void onGoodbyeButtonClicked(){
    presenter.greetGoodbye();
  }

  @Override
  public void showHello(String greetingText){
    greetingTextView.setTextColor(Color.RED);
    greetingTextView.setText(greetingText);
  }

  @Override
  public void showGoodbye(String greetingText){
    greetingTextView.setTextColor(Color.BLUE);
    greetingTextView.setText(greetingText);
  }
}