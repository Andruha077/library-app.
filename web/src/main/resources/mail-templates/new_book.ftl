
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Нова книга в каталозі BookApp</title>
</head>
<body style="margin:0; padding:30px 15px; background-color:#f6f5f1; font-family:Arial,sans-serif; color:#17231f;">

<div style="max-width:600px; margin:0 auto; padding:30px; background-color:#ffffff; border-radius:12px;">

    <div style="text-align:center; margin-bottom:25px;">
        <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
             style="width:150px; margin-bottom:20px;"
             alt="BookApp">

        <h1 style="color:#254b3c; margin:0;">
            BookApp
        </h1>
        <p style="color:#777777; margin-top:8px;">
            Нова книга в каталозі
        </p>
    </div>

    <div style="padding:20px; background-color:#f6f5f1; border-left:4px solid #254b3c; border-radius:6px;">

        <h2 style="color:#254b3c; margin-top:0;">
            ${title?html}
        </h2>

        <p><b>Автор:</b> ${author?html}</p>

        <p>
            <b>Рік видання:</b>
            <#if year??>
                ${year?c}
            <#else>
                Не вказано
            </#if>
        </p>

        <#if year?? && year < 2000>
            <p style="color:#a36b00; font-weight:bold;">
                Раритетне видання!
            </p>
        </#if>

        <p style="margin-bottom:0;">
            <b>Дата додавання:</b> ${added?html}
        </p>

    </div>

    <div style="margin-top:25px; text-align:center; color:#888888; font-size:12px;">
        <p>Дякуємо, що користуєтеся BookApp!</p>
        <p>BookApp © 2026</p>
    </div>

</div>

</body>
</html>
