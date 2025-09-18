package com.example.plantdiseases;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {

    DisplayMetrics displayMetrics = new DisplayMetrics();

    private int screenWidth, screenHeight;

    private TextView textWelcome, textInfo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        textWelcome = findViewById(R.id.textWelcome);
        textInfo = findViewById(R.id.textInfo);

        if (textWelcome == null || textInfo == null) {
            Log.e("MENU_ACTIVITY", "Views not found: textWelcome=" + (textWelcome == null ? "null" : "found") +
                    ", textInfo=" + (textInfo == null ? "null" : "found"));
            Toast.makeText(this, "Ошибка: UI элементы не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("MENU_ACTIVITY", "Firestore initialization failed");
            Toast.makeText(this, "Ошибка инициализации Firestore", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = getIntent().getStringExtra("category");
        if (category == null || category.isEmpty()) {
            Log.e("MENU_ACTIVITY", "No category provided in Intent");
            textWelcome.setText("Ошибка");
            textInfo.setText("Категория не выбрана");
            Toast.makeText(this, "Категория не выбрана", Toast.LENGTH_SHORT).show();
            return;
        }

        textWelcome.setText(category);
        if (category.equals("Fungal")) {
            String htmlText = "<h2>Фитофтороз (Late blight)</h2>" +
                    "<h3>Возбудитель.</h3>" +
                    "<img src='fungal_reino_protista'/><br/>" +
                    "<p style='font-size: 20px; font-weight: 900; text-align: center;'>Phytophthora infestans (оомицет)</p>" +
                    "<h3>Круг хозяев</h3>"+
                    "<p>Томат, перец, баклажан, картофель и др. паслёновые.</p><br/>"+

                    "<h3>Симптомы</h3>" +
                    "<h5><i>Листья:</i></h5>" +
                    "<p>⦁ <i>Ранние признаки: на верхней стороне листьев появляются небольшие, неправильной формы, водянистые пятна от светло-зелёного до тёмно-зелёного цвета.</i><br/>" +
                    "⦁ <i>Развитие болезни: пятна быстро увеличиваются, становятся буро-коричневыми или пурпурно-чёрными, вокруг них формируется хлоротичная (желтоватая) зона.</i><br/>" +
                    "⦁ <i>Поздняя стадия: на нижней стороне листьев, по краям поражённых участков, появляется белый пушистый налёт — спороношение патогена (Phytophthora infestans).</i></p>" +
                    "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_late_blight' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200' /><br/>" +
                    "</div>" +

                    "<h3>Стебли и черешки:</h3>" +
                    "<p>⦁ <i>Формируются продолговатые, тёмно-бурые или чёрные некротические поражения.</i><br/>" +
                    "⦁ <i>При высокой влажности возможно спороношение на поверхности поражённых тканей.</i><br/>" +
                    "⦁ <i>В местах поражения ткани становятся ломкими, что приводит к полеганию растений.</i></p>" +

                    "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_phytophthora_infestans_root', width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_3' width='500' height='200' /><br/>" +
                    "</div>" +


                    "<h4>Плоды:</h4>" +
                    "<p>⦁ Поражение обычно начинается с верхней части плода.<br/>" +
                    "⦁ На поверхности формируются бурые твёрдые пятна, иногда с серо-зелёным оттенком.<br/>" +
                    "⦁ Ткани остаются плотными, но со временем покрываются вторичной инфекцией и загнивают.</p>" +

                    "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_tomato', width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_2' width='500' height='200' /><br/>" +
                    "</div>" +

                    "<h4>Фитофтороз (общее изображение):</h4>" +
                    "<img src='fungal_late_blight' width='300' height='200' /><br/>" +
                    "<p>Общее изображение симптомов фитофтороза на растениях.</p>" +
                    "<h4>Фитофтороз (дополнительное изображение):</h4>" +
                    "<img src='fungal_late_blight_2' width='300' height='200' /><br/>" +
                    "<p>Дополнительное изображение симптомов фитофтороза на растениях.</p>" +
                    "<h3>Эпидемиология и жизненный цикл</h3>" +
                    "<p><b>Источники инфекции:</b><br/>" +
                    "⦁ растительные остатки томата, картофеля и других паслёновых;<br/>" +
                    "⦁ заражённые семенные клубни (у картофеля);<br/>" +
                    "⦁ сорняки из семейства паслёновых (например, паслён чёрный);<br/>" +
                    "⦁ соседние посадки заражённых культур.</p>" +
                    "<p><b>Развитие и распространение:</b><br/>" +
                    "⦁ Первичное заражение происходит при контакте спор с влажными листьями или стеблями.<br/>" +
                    "⦁ При высокой влажности на поражённых тканях формируется спороношение. Споры распространяются ветром, дождевыми каплями, насекомыми и механически (через людей, инвентарь, животных).<br/>" +
                    "⦁ Инфекция распространяется очень быстро: новые очаги могут появляться в течение 2–3 дней после первичного заражения.</p>" +
                    "<p><b>Климатические условия, способствующие развитию:</b><br/>" +
                    "⦁ Оптимальная температура: 10–25 °C (с наиболее активным развитием при 15–20 °C).<br/>" +
                    "⦁ Влажность воздуха выше 75–80% или длительное удержание влаги на листьях (роса, туманы, дожди).<br/>" +
                    "⦁ Резкие перепады дневной и ночной температуры.<br/>" +
                    "⦁ Дождевое орошение и загущенные посадки, препятствующие вентиляции.</p>" +
                    "<p><b>Особенности эпидемиологии:</b><br/>" +
                    "⦁ На открытых посевах развивается очагами, но в теплицах может принимать характер эпифитотии.<br/>" +
                    "⦁ При благоприятных условиях болезнь способна уничтожить урожай томата за 7–10 дней.</p>" +
                    "<h3>Меры борьбы и профилактика</h3>" +
                    "<p><b>Агротехнические меры:</b><br/>" +
                    "⦁ Соблюдение севооборота: возвращение паслёновых культур на прежнее место не ранее чем через 3–4 года.<br/>" +
                    "⦁ Полное удаление и уничтожение растительных остатков после уборки урожая.<br/>" +
                    "⦁ Использование здорового посадочного материала (сертифицированные растения и клубни).<br/>" +
                    "⦁ Обеспечение хорошей вентиляции в теплицах, предотвращение загущённых посадок.<br/>" +
                    "⦁ Полив преимущественно под корень, исключение дождевого орошения.<br/>" +
                    "⦁ Поддержание оптимального микроклимата (снижение влажности, улучшение циркуляции воздуха).</p>" +
                    "<p><b>Биологические методы:</b><br/>" +
                    "⦁ Использование микробиологических препаратов на основе грибов и бактерий для профилактических обработок (Trichoderma spp., Bacillus subtilis), которые подавляют развитие патогена.</p>" +
                    "<p><b>Химические методы:</b><br/>" +
                    "⦁ Контактные фунгициды: медьсодержащие препараты (бордосская смесь, хлорокись меди, гидроксид меди).<br/>" +
                    "⦁ Системные фунгициды: действующие вещества — металаксил, мандипропамид, цимоксанил, циазофамид и др.<br/>" +
                    "⦁ Применение препаратов строго по регламенту с учётом периода ожидания и ротации действующих веществ для предотвращения резистентности.</p>" +
                    "<p><b>Интегрированная защита растений (IPM):</b><br/>" +
                    "⦁ Сочетание агротехнических, биологических и химических приёмов.<br/>" +
                    "⦁ Регулярный фитосанитарный мониторинг посевов.<br/>" +
                    "⦁ Применение прогнозных моделей (учёт температуры, влажности и осадков) для определения сроков обработок.<br/>" +
                    "⦁ Минимизация применения химических средств за счёт профилактических и биологических методов.</p>";

            textInfo.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int resId = getResources().getIdentifier(source, "drawable", getPackageName());
                    if (resId == 0) {
                        Log.w("MENU_ACTIVITY", "Image resource not found: " + source);
                        Drawable placeholder = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
                        placeholder.setBounds(0, 0, 300, 200);
                        return placeholder;
                    }

                    Drawable drawable = getResources().getDrawable(resId);

                    if (source.equals("fungal_reino_protista")) {
                        drawable.setBounds((screenWidth - 700) / 2 - 70, 0, (screenWidth + 700) / 2, 400);
                    }
                    else if (source.equals("fungal_phytophthora") || source.equals("fungal_phytophthora_infestans_late") || source.equals("fungal_late_blight")) {
                        drawable.setBounds((screenWidth - 700) / 2 - 70, 0, (screenWidth + 700) / 2, 400);
                    }
//                    int originalHeight = drawable.getIntrinsicHeight();
//                    drawable.setBounds(
//                            (screenWidth - 800) / 2,0, (screenWidth + 800) / 2,originalHeight
//                    );
                    else if (source.equals("fungal_phytophthora_infestans_root") || source.equals("fungal_late_blight_3")) {
                        drawable.setBounds((screenWidth - 700) / 2 - 70, 0, (screenWidth + 700) / 2, 400);
                    }
                    else if (source.equals("fungal_tomato") || source.equals("fungal_late_blight_2")) {
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 500;

                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        drawable.setBounds((screenWidth - maxWidth) / 2, 0, (screenWidth + maxWidth) / 2, newHeight);
                    }

                    else if (source.equals("fungal_phytophthora_infestans")) {
                        drawable.setBounds( 700, 0, 700, 700);
                    } else {
                        drawable.setBounds(0, 0, 300, 200);
                    }

                    return drawable;
                }
            }, null));

        } else {
            textInfo.setText("Информация о категории " + category + " пока недоступна.");
        }

    }

}

