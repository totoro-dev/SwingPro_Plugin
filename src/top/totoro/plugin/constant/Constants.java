package top.totoro.plugin.constant;

public class Constants {
    public static final String SWING_FILE_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
    public static final String DEFAULT_SWING_FILE_CONTENT = SWING_FILE_HEADER +
            "<LinearLayout\n" +
            "\tid=\"main_linear_layout\"\n" +
            "\twidth=\"match_parent\"\n" +
            "\theight=\"match_parent\"\n" +
            "\torientation=\"vertical\">\n" +
            "\t<TextView\n" +
            "\t\twidth=\"match_parent\"\n" +
            "\t\theight=\"match_parent\"\n" +
            "\t\tbackground=\"#15a5e5\"\n" +
            "\t\ttextColor=\"#eeeeee\"\n" +
            "\t\ttext=\"Hello World\"\n" +
            "\t\tid=\"tv_test\"/>\n" +
            "\t<CenterLayout\n" +
            "\t\twidth=\"match_parent\"\n" +
            "\t\theight=\"match_parent\"\n" +
            "\t\torientation=\"vertical\">\n" +
            "\t\t<CenterLayout\n" +
            " \t\t\twidth=\"match_parent\"\n" +
            "\t\t\theight=\"wrap_content\"\n" +
            "\t\t\torientation=\"horizontal\">\n" +
            "\t\t<Button\n" +
            "\t\t\twidth=\"wrap_content\"\n" +
            "\t\t\theight=\"wrap_content\"\n" +
            "\t\t\tbackground=\"#15a5e5\"\n" +
            "\t\t\ttextColor=\"#ffffff\"\n" +
            "\t\t\ttextSize=\"14\"\n" +
            "\t\t\ttext=\"跳转页面\"\n" +
            "\t\t\tid=\"btn_test\"/>\n" +
            "\t\t</CenterLayout>\n" +
            "\t</CenterLayout>\n" +
            "\n" +
            "</LinearLayout>";

    public static final String NEW_SWING_FILE_CONTENT = SWING_FILE_HEADER +
            "<LinearLayout\n" +
            "\twidth=\"match_parent\"\n" +
            "\theight=\"match_parent\"\n" +
            "\torientation=\"vertical\">\n" +
            "\t<TextView\n" +
            "\t\twidth=\"match_parent\"\n" +
            "\t\theight=\"match_parent\"\n" +
            "\t\ttextColor=\"#515151\"\n" +
            "\t\ttext=\"Hello World\"/>\n" +
            "</LinearLayout>";

    public static final String DEFAULT_STYLES_FILE_CONTENT = SWING_FILE_HEADER +
            "<resource>\n" +
            "\t<default>\n" +
            "\t\t<item name=\"backgroundColor\">#ffffff</item>\n" +
            "\t\t<item name=\"borderColor\">#dbdbdb</item>\n" +
            "\t\t<item name=\"themeColor\">#15a5e5</item>\n" +
            "\t\t<item name=\"appIcon\">img/swing_logo.png</item>\n" +
            "\t</default>\n" +
            "</resource>";

    public static final String DEFAULT_R_FILE_CONTENT = "package swing;\n\n" +
            "//\n" +
            "// Source code created by IntelliJ IDEA, don't redefine anyway!\n" +
            "// (powered by SwingPro Plugin)\n" +
            "//"+
            "\n\n" +
            "public final class R{\n" +
            "    \n" +
            "}";

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

    public static final String DEPENDENCY = "\t<dependencies>\n" +
            "\t\t<dependency>\n" +
            "\t\t\t<groupId>io.github.totoro-dev</groupId>\n" +
            "\t\t\t<artifactId>SwingPro</artifactId>\n" +
            "\t\t\t<version>1.0.3</version>\n" +
            "\t\t</dependency>\n" +
            "\t</dependencies>\n";
}
