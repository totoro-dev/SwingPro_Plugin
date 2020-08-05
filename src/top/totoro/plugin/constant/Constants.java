package top.totoro.plugin.constant;

public class Constants {
    public static final String SWING_FILE_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
    public static final String DEFAULT_SWING_FILE_CONTENT = SWING_FILE_HEADER +
            "<LinearLayout\n" +
            "    id=\"main_linear_layout\"\n" +
            "    width=\"match_parent\"\n" +
            "    height=\"match_parent\"\n" +
            "    orientation=\"vertical\">\n" +
            "    <TextView\n" +
            "        width=\"match_parent\"\n" +
            "        height=\"match_parent\"\n" +
            "        background=\"#15a5e5\"\n" +
            "        textColor=\"#eeeeee\"\n" +
            "        text=\"Hello World\"\n" +
            "        id=\"tv_test\"/>\n" +
            "    <CenterLayout\n" +
            "        width=\"match_parent\"\n" +
            "        height=\"match_parent\"\n" +
            "        orientation=\"vertical\">\n" +
            "        <CenterLayout\n" +
            "            width=\"match_parent\"\n" +
            "            height=\"wrap_content\"\n" +
            "            orientation=\"horizontal\">\n" +
            "        <Button\n" +
            "            width=\"wrap_content\"\n" +
            "            height=\"wrap_content\"\n" +
            "            background=\"#15a5e5\"\n" +
            "            textColor=\"#ffffff\"\n" +
            "            textSize=\"14\"\n" +
            "            text=\"跳转页面\"\n" +
            "            id=\"btn_test\"/>\n" +
            "        </CenterLayout>\n" +
            "    </CenterLayout>\n" +
            "\n" +
            "</LinearLayout>";

    public static final String NEW_SWING_FILE_CONTENT = SWING_FILE_HEADER + "<LinearLayout\n" +
            "        width=\"match_parent\"\n" +
            "        height=\"match_parent\"\n" +
            "        orientation=\"vertical\">\n" +
            "        <TextView\n" +
            "                width=\"match_parent\"\n" +
            "                height=\"match_parent\"\n" +
            "                textColor=\"#515151\"\n" +
            "                text=\"Hello World\"/>\n" +
            "</LinearLayout>";

    public static final String DEFAULT_MAIN_ACTIVITY_CONTENT = "package ui;\n" +
            "\n" +
            "import swing.R;\n" +
            "import top.totoro.swing.widget.base.Size;\n" +
            "import top.totoro.swing.widget.context.Activity;\n" +
            "import top.totoro.swing.widget.listener.OnClickListener;\n" +
            "import top.totoro.swing.widget.view.View;\n" +
            "\n" +
            "import static top.totoro.swing.widget.base.BaseAttribute.GONE;\n" +
            "\n" +
            "public class MainActivity extends Activity {\n" +
            "    @Override\n" +
            "    public void onCreate() {\n" +
            "        super.onCreate();\n" +
            "        setContentView(R.layout.activity_main);\n" +
            "        findViewById(R.id.btn_test).addOnClickListener(new OnClickListener() {\n" +
            "            public void onClick(View view) {\n" +
            "                startActivity(MainActivity.this, TargetActivity.class);\n" +
            "            }\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        /* 可以指定窗体的大小 */\n" +
            "        newInstance(new Size(500, 500)).startActivity(MainActivity.class);\n" +
            "    }\n" +
            "\n" +
            "    public static class TargetActivity extends Activity {\n" +
            "        @Override\n" +
            "        public void onCreate() {\n" +
            "            super.onCreate();\n" +
            "            setContentView(R.layout.activity_main);\n" +
            "            findViewById(R.id.btn_test).setVisible(GONE);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    public static final String DEPENDENCY = "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>io.github.totoro-dev</groupId>\n" +
            "            <artifactId>SwingPro</artifactId>\n" +
            "            <version>1.0.1</version>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n";
}
