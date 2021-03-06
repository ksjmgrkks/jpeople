package com.example.myapplication.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class TermsActivity extends AppCompatActivity {

    private TextView textViewTerms;
    private TextView textViewPrivacy;
    private Button buttonConfirm;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("");

        textViewTerms = findViewById(R.id.textView_terms);
        textViewPrivacy = findViewById(R.id.textView_privacy);
        buttonConfirm = findViewById(R.id.button_confirm);

        CheckBox checkboxTerms = (CheckBox) findViewById(R.id.checkbox_terms) ;
        CheckBox checkboxPrivacy = (CheckBox) findViewById(R.id.checkbox_privacy);

        textViewTerms.setText("제 1 장 총 칙\n" +
                "\n" +
                "제1조 (목적)\n" +
                "본 약관은 제이피플(이하 \"회사\")이 제공하는 JPeople 서비스 (이하 \"서비스\")와 관련하여\n" +
                "이용조건 및 절차,기타 필요한 사항을 규정함을 목적으로 합니다.\n" +
                "\n" +
                "제2조 (약관의 효력 및 변경)\n" +
                "1. 본 약관의 내용은 회사가 운영하는 서비스의 화면에 게시하거나\n" +
                "기타의 방법으로 회원에게 공지함으로써 효력이 발생됩니다.\n" +
                "2. 서비스는 중요한 사유가 있을 때 이 약관을 변경할 수 있으며,\n" +
                "변경된 약관은 제1항의 방법으로 공지함으로써 효력이 발생합니다.\n" +
                "3. 변경된 약관의 효력은 변경 전에 가입한 회원 전체에게 소급 적용됩니다.\n" +
                "\n" +
                "제3조 (약관 외 준칙)\n" +
                "본 약관에 명시되지 아니한 사항에 대해서는 약관의 규제에 관한 법률,\n" +
                "콘텐츠산업진흥법, 정보통신망 이용촉진 및 정보보호 등에 관한 법률\n" +
                "및 기타 관련 법령의 규정된 바에 따릅니다.\n" +
                "\n" +
                "제4조 (용어의 정의)\n" +
                "본 약관에서 사용하는 주요한 용어의 정의는 다음과 같습니다.\n" +
                "1. 서비스 : 회사가 운영하는 인터넷사이트 등에서 제공하는 인터넷상의\n" +
                "   모든 서비스(무선인터넷 서비스 포함)를 말합니다.\n" +
                "2. 이용자 : 회사가 제공하는 서비스를 받는 회원 및 비회원\n" +
                "3. 회원 : 회사와 서비스 이용 계약을 체결하고 아이디를 부여 받아 회사가\n" +
                "   제공하는 서비스를 이용하는 모든 이용자를 의미하며, 다음 각호를 포함합니다.\n" +
                "4. 아이디 : 회원의 식별과 서비스의 이용을 위하여 회원이 직접 설정하고,\n" +
                "   회사가 승인하여 등록된 회원이 정한 문자와 숫자의 조합을 말합니다.\n" +
                "5. 비밀번호 : 회원이 부여 받은 아이디와 일치된 회원임을 확인하고,\n" +
                "   회원 자신의 비밀을 보호하기 위하여 회원이 정한 문자와 숫자의 조합을 말합니다.\n" +
                "7. 이용 중지 : 회사가 약관에 의거하여 회원의 서비스 이용을 제한하는 것을 말합니다.\n" +
                "8. 해지 : 회사 또는 회원이 이용 계약을 중지하는 것을 말합니다.\n" +
                "10. “콘텐츠”라 함은 『정보통신망 이용촉진 및 정보 보호 등에 관한 법률』\n" +
                "    제2조 제1항 제1호의 규정에 의한 정보통신망에서 사용되는 부호, 문자, 음성, 음향, 이미지\n" +
                "    또는 영상 등으로 표현된 자료 또는 정보로서, 그 보존 및 이용에 있어서 효용을 높일 수 있도록\n" +
                "    전자적 형태로 제작 또는 처리된 것을 말합니다.\n" +
                "11. “디바이스(device)”라 함은 “회사”가 제공하는 “콘텐츠”를 이용할 수 있는 PC, Tablet,\n" +
                "    스마트폰을 의미하며, 향후 출시되는 JPeople 서비스가\n" +
                "    이용 가능한 디지털 전자 기기를 모두 포함합니다.\n" +
                "    본 조에서 정의된 용어 이외의 용어에 대해서는 관계 법령 및 서비스별 안내에서 정의하는 바에 따릅니다.\n" +
                "\n" +
                "제 2 장 서비스 이용 계약\n" +
                "\n" +
                "제1조 (이용계약의 성립)\n" +
                "1. 이용자는 회사가 정한 가입 양식에 따라 회원정보를 기입한 후\n" +
                "   본 약관에 동의한다는 의사표시를 함으로서 회원가입을 신청합니다.\n" +
                "2. 이용 계약은 회사 회원가입 희망자가 이용약관 동의 후 이용 신청에 대하여 회사가 승낙함으로써 성립합니다.\n" +
                "\n" +
                "제2조 (이용신청)\n" +
                "1. 회원으로 가입하여 서비스를 이용하기를 희망하는 자는 약관의 내용에 대하여 동의를 한 다음\n" +
                "   회사가 정한 소정 양식에 따라 요청하는 개인 인적 사항을 제공하여 이용 신청을 합니다.\n" +
                "2. 회원은 신규회원가입 화면에서 회사가 정한 소정 양식에 따라\n" +
                "   요청하는 개인 인적 사항을 제공하여 이용신청을 합니다.\n" +
                "3. 회사는 이용신청 신규 회원 중 온라인상 공인된 본인 확인이 \n" +
                "   어려운 회원에 한하여 추가 확인 절차를 요구할 수 있습니다.\n" +
                "\n" +
                "제3조 (이용신청의 승낙)\n" +
                "1. 회사는 제 6 조에 따른 이용 신청에 대하여 특별한 사정이 없는 한 접수 순서대로 이용 신청을 승낙합니다.\n" +
                "2. 회사는 다음 각 호에 해당하는 경우 이용 신청에 대한 승낙을 제한할 수 있고,\n" +
                "   그 사유가 해소될 때까지 승낙을 유보할 수 있습니다.\n" +
                "   가. 서비스 관련 설비에 여유가 없는 경우\n" +
                "   나. 기술상 지장이 있는 경우\n" +
                "   다. 기타 회사의 사정상 필요하다고 인정되는 경우\n" +
                "3. 회사는 다음 각 호에 해당하는 사항을 인지하는 경우\n" +
                "   동 이용계약 신청을 별도의 통지 없이 승낙하지 아니할 수 있습니다.\n" +
                "   가. 다른 사람의 명의를 사용하여 신청한 경우\n" +
                "   나. 이용 신청 시 필요 사항을 허위로 기재하여 신청한 경우\n" +
                "   다. 사회의 안녕과 질서 혹은 미풍양속을 저해할 목적으로 신청한 경우\n" +
                "   라. 기타 회사가 정한 이용 신청 요건이 미비 된 경우\n" +
                "   마. 악성 프로그램 및 버그를 이용하거나 시스템 취약점을 악용하는 등 부정한 방법을 서비스에 사용한 경우\n" +
                "   바. 이용자가 이 약관에 의하여 이전에 회원 자격을 상실한 적이 있는 경우.\n" +
                "    다만, 회사의 회원 재가입 승낙을 받은 경우는 예외로 합니다.\n" +
                "\n" +
                "제4조 (개인정보의 보호)\n" +
                "1. 회사는 이용자 등록정보를 포함한 이용자의 개인정보를 보호하기 위해\n" +
                "   관계법령이 정하는 바와 개인정보 보호정책을 준수합니다.\n" +
                "   자세한 사항은 \"개인정보처리방침\"에서 확인하실 수 있습니다.\n" +
                "2. 이용자가 게시판, 메일, 채팅 등 온라인상에서 자발적으로 제공하는 개인정보는\n" +
                "   다른 사람이 수집하여 사용할 가능성이 있으며 이러한 위험까지 회사가 부담하지는 않습니다.\n" +
                "\n" +
                "제5조 (회원정보의 변경)\n" +
                "1. 회원은 '환경설정' 메뉴를 통해 언제든지 본인의 수정 가능한 개인정보를 열람하고 수정할 수 있습니다.\n" +
                "2. 회원은 이용 신청 시 기재한 사항이 변경되었을 경우 해당 내용을 수정해야 하며,\n" +
                "   회원정보를 변경하지 아니하여 발생되는 문제의 책임은 회원에게 있습니다.");
        textViewPrivacy.setText("개인정보처리방침\n" +
                "\n" +
                "제이피플(이하 '회사')은 개인정보보호법에 따라 이용자의 개인정보 보호 및\n" +
                "권익을 보호하고 개인정보와 관련한 이용자의 고충을 원활하게 처리할 수 있도록\n" +
                "다음과 같은 처리방침을 두고 있습니다.\n" +
                "\n" +
                "회사는 개인정보처리방침을 개정하는 경우\n" +
                "웹사이트 공지사항(또는 개별공지)을 통하여 공지할 것입니다.\n" +
                "\n" +
                "○ 본 방침은부터 2021년 04월 01일부터 시행됩니다.\n" +
                "\n" +
                "1. 개인정보의 처리 목적\n" +
                "회사는 개인정보를 다음의 목적을 위해 처리합니다.\n" +
                "처리한 개인정보는 다음의 목적이외의 용도로는 사용되지 않으며\n" +
                "이용 목적이 변경될 시에는 사전동의를 구할 예정입니다.\n" +
                "\n" +
                "가. 홈페이지 회원가입 및 관리\n" +
                "- 회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증,\n" +
                "  회원자격 유지·관리, 제한적 본인확인제 시행에 따른\n" +
                "  본인확인, 서비스 부정이용 방지,\n" +
                "  각종 고지·통지, 고충처리, 분쟁 조정을 위한 기록 보존 등을\n" +
                "  목적으로 개인정보를 처리합니다.\n" +
                "\n" +
                "나. 민원사무 처리\n" +
                "- 민원인의 신원 확인, 민원사항 확인, 사실조사를 위한 연락·통지,\n" +
                "  처리결과 통보 등을 목적으로 개인정보를 처리합니다.\n" +
                "\n" +
                "다. 재화 또는 서비스 제공\n" +
                "- 콘텐츠 제공, 맞춤 서비스 제공, 본인인증 등을 목적으로\n" +
                "  개인정보를 처리합니다.");

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkboxTerms.isChecked()) {
                    Toast.makeText(getApplicationContext(), "이용약관 동의에 체크해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkboxPrivacy.isChecked()) {
                    Toast.makeText(getApplicationContext(), "개인정보처리방침 동의에 체크해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(TermsActivity.this, SignupActivity.class);
                TermsActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}