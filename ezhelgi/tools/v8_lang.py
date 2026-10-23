"""v8: тексты (ru_ru, kk_kz, en_us) — праздники аула, айтыс, жыр, бата, мүшел, асық, одежда."""

N = {}


def add(key, ru, kk, en):
    N[key] = (ru, kk, en)


def many(prefix, items):
    for i, v in enumerate(items):
        add(f"{prefix}.{i}", *v)


# ------------------------------------------------------------------ сущности и предметы
for k, v in {
    "shabandoz": ("Шабандоз (всадник)", "Шабандоз", "Shabandoz (Rider)"),
    "serke": ("Серке (туша козла для көкпара)", "Серке", "Serke (Kokpar Goat)"),
    "aqyn": ("Ақын", "Ақын", "Aqyn (Poet)"),
    "kelin": ("Келін (невеста)", "Келін", "Kelin (Bride)"),
    "zhyrshy": ("Жыршы (сказитель)", "Жыршы", "Zhyrshy (Storyteller)"),
    "saqa": ("Сақа", "Сақа", "Saqa"),
}.items():
    add(f"entity.ezhelgi.{k}", *v)
for k, v in {
    "asyq": ("Асық", "Асық", "Asyq (Knucklebone)"),
    "saqa": ("Сақа (биток для асыков)", "Сақа", "Saqa (Shooter Bone)"),
    "shapan": ("Шапан", "Шапан", "Shapan Robe"),
    "kamzol": ("Камзол", "Камзол", "Kamzol Vest"),
    "taqiya": ("Тақия (тюбетейка)", "Тақия", "Taqiya Skullcap"),
    "kimeshek": ("Кимешек", "Кимешек", "Kimeshek Headdress"),
    "saukele": ("Сәукеле (свадебный убор)", "Сәукеле", "Saukele (Bridal Headdress)"),
    "uki_borik": ("Бөрік с перьями үкі", "Үкілі бөрік", "Fur Hat with Owl Feathers"),
    "kelin_koilek": ("Свадебное платье келін", "Келін көйлегі", "Bride's Dress"),
    "kumis_belbeu": ("Наборный пояс с серебром", "Күміс белбеу", "Silver Belbeu Belt"),
    "altyn_belbeu": ("Наборный пояс с золотом", "Алтын белбеу", "Golden Belbeu Belt"),
    "kelin_spawn_egg": ("Яйцо призыва: келін", "Келін жұмыртқасы", "Kelin Spawn Egg"),
    "zhyrshy_spawn_egg": ("Яйцо призыва: жыршы", "Жыршы жұмыртқасы", "Zhyrshy Spawn Egg"),
}.items():
    add(f"item.ezhelgi.{k}", *v)
add("block.ezhelgi.asyq_qatar", "Асық қатар (ряд асыков)", "Асық қатар", "Row of Asyqs")
for k, v in {
    "shapan": ("В снегах греет и защищает", "Қарда жылытады әрі қорғайды", "Keeps you warm and safe in the snow"),
    "kamzol": ("Лёгкость: ходишь быстрее", "Жеңілдік: жылдам жүресің", "Lightness: you move faster"),
    "taqiya": ("Удача", "Сәттілік", "Luck"),
    "kimeshek": ("Больше здоровья", "Денсаулық артады", "More health"),
    "saukele": ("Большая удача; жители уважают и делают скидки", "Үлкен бақ; жұрт құрметтейді",
                "Great luck; villagers honour you with discounts"),
    "uki_borik": ("Үкі бережёт от сглаза и порчи; видишь в темноте", "Үкі көз тиюден, сұқтан сақтайды; қараңғыда көресің",
                  "Owl feathers ward off curses; night vision"),
    "kelin_koilek": ("Медленное исцеление", "Баяу жазылу", "Slow healing"),
}.items():
    add(f"tooltip.ezhelgi.clothes.{k}", *v)

# ------------------------------------------------------------------ асық ату
add("message.ezhelgi.asyq.hit", "Выбито асыков: %s!", "%s асық ұттың!", "You knocked out %s asyq!")
add("message.ezhelgi.asyq.miss", "Мимо! Сақа лишь задела ряд", "Тимеді! Сақа қатарды сипап өтті", "Miss! The saqa just grazed the row")
add("message.ezhelgi.asyq.empty", "Ряд пуст — подожди, дети снова выставят асыки", "Қатар бос — балалар қайта тізеді",
    "The row is empty — wait for the children to set it up again")
add("message.ezhelgi.asyq.child_need", "Дети меняются, если у тебя хотя бы 4 асыка", "Кемінде 4 асық болса, балалар айырбастайды",
    "Children trade if you have at least 4 asyqs")
add("message.ezhelgi.asyq.child_trade", "Ты отдал %s асыков и получил: %s", "%s асық беріп, %s алдың", "You gave %s asyqs and got: %s")

# ------------------------------------------------------------------ бата и мүшел
for k, v in {
    "luck": ("Аксакал даёт бату: «Бағың ашылсын!» — удача на час.", "Ақсақал бата берді: «Бағың ашылсын!»",
             "The elder blesses you: \"May fortune smile on you!\" Luck for an hour."),
    "regen": ("Аксакал даёт бату: «Денің сау болсын!» — здоровье на час.", "Ақсақал бата берді: «Денің сау болсын!»",
              "The elder blesses you: \"Be healthy!\" Regeneration for an hour."),
    "health": ("Аксакал даёт бату: «Қуатың арта берсін!» — больше сил на час.", "Ақсақал бата берді: «Қуатың арта берсін!»",
               "The elder blesses you: \"May your strength grow!\" Extra health for an hour."),
    "speed": ("Аксакал даёт бату: «Жолың ашық болсын!» — лёгкий путь на час.", "Ақсақал бата берді: «Жолың ашық болсын!»",
              "The elder blesses you: \"May your road be open!\" Speed for an hour."),
    "haste": ("Аксакал даёт бату: «Ісің оңға басcын!» — спорая работа на час.", "Ақсақал бата берді: «Ісің оңға бассын!»",
              "The elder blesses you: \"May your work go well!\" Haste for an hour."),
    "resist": ("Аксакал даёт бату: «Пәледен аман бол!» — защита на час.", "Ақсақал бата берді: «Пәледен аман бол!»",
               "The elder blesses you: \"Be safe from harm!\" Resistance for an hour."),
}.items():
    add(f"aqsaqal.ezhelgi.bata_give.{k}", *v)
add("message.ezhelgi.mushel.start", "Наступил мүшел — %s-й день. Говорят, в такие дни нечисть сильнее!",
    "Мүшел келді — %s-күн. Мұндай күндері жын-пері күшейеді дейді!",
    "Mushel has come — day %s. They say evil grows stronger on such days!")
add("message.ezhelgi.mushel.desc", "До рассвета враги крепче и носят броню. Будь осторожен, батыр.",
    "Таң атқанша жаулар мықты, сауыт киген. Абай бол, батыр.",
    "Until dawn enemies are tougher and wear armour. Be careful, batyr.")

# ------------------------------------------------------------------ праздники
many("aul.ezhelgi.toi", [
    ("Сүйінші! Сегодня в ауле той — свадьба! Молодые сыграют беташар.", "Сүйінші! Бүгін ауылда той! Беташар болады.",
     "Good news! Today there is a toi — a wedding in the aul!"),
    ("Келін, поклонись родне жениха. Пусть дом ваш будет полной чашей!", "Келін, ата-енеңе, ағайынға сәлем сал. Шаңырағың биік болсын!",
     "Kelin, bow to your new family. May your home be a full cup!"),
    ("Бросаем шашу — сладости на счастье! Подбирай, не стесняйся.", "Шашу шашамыз — бақ үшін! Тер, ұялма.",
     "We throw shashu — sweets for luck! Pick them up."),
    ("Гостям — угощение: подойди к любому жителю аула.", "Қонақтарға дәм: ауылдың кез келген тұрғынына бар.",
     "Treats for guests: talk to any villager of the aul."),
    ("Кюйши, играй! Пусть домбыра звенит до вечера!", "Күйші, тарт! Домбыра кешке дейін шертілсін!", "Kuishi, play! Let the dombyra ring till evening!"),
])
add("aul.ezhelgi.toi.treat", "Тебя угостили баурсаками, казы и кумысом. Той тойға ұлассын!",
    "Сені бауырсақ, қазы, қымызбен сыйлады. Той тойға ұлассын!", "You were treated to baursaks, kazy and kumys. May one feast follow another!")
add("aul.ezhelgi.toi.end", "Той закончился. Гости разъезжаются, а келін остаётся жить в ауле.",
    "Той бітті. Қонақтар тарады, келін ауылда қалды.", "The toi is over. Guests depart; the bride stays to live in the aul.")
many("aul.ezhelgi.aitys", [
    ("Айтыс! К нам приехали два акына — сейчас начнут состязаться в стихах.", "Айтыс! Екі ақын келді — қазір сөз сайысы басталады.",
     "Aitys! Two poets have come to compete in verse."),
    ("Слушайте внимательно: острое слово дороже золота.", "Мұқият тыңдаңдар: өткір сөз алтыннан қымбат.", "Listen well: a sharp word is worth more than gold."),
    ("Когда закончат — вы скажете, кто победил.", "Біткен соң кімнің жеңгенін сендер айтасыңдар.", "When they finish, you will say who won."),
])
many("aul.ezhelgi.aitys.vote", [
    ("Ну что, люди? Кто сегодня был острее на язык?", "Ал, жұртым? Бүгін кімнің тілі өткір болды?", "Well, people? Whose tongue was sharper today?"),
    ("Нажми ПКМ по акыну, которого считаешь победителем.", "Жеңімпаз деп санаған ақынды басып таңда.", "Right-click the poet you think won."),
])
add("aul.ezhelgi.aitys.wait", "Айтыс ещё идёт — дослушай!", "Айтыс әлі жүріп жатыр — тыңдап біт!", "The aitys isn't over — keep listening!")
add("aul.ezhelgi.aitys.winner", "Победитель айтыса — %s! Аул дарит ему шапан и коня.", "Айтыс жеңімпазы — %s! Ауыл оған шапан мен ат сыйлайды.",
    "The winner of the aitys is %s! The aul gives a shapan and a horse.")
add("aul.ezhelgi.aitys.judge", "За честный суд аксакал дал тебе немного золота.", "Әділ төрелігің үшін ақсақал алтын берді.",
    "For fair judgement the elder gave you some gold.")
many("aitys.ezhelgi.name", [("Бекжан ақын", "Бекжан ақын", "Bekzhan the Poet"), ("Ақтоты ақын", "Ақтоты ақын", "Aqtoty the Poet"),
                            ("Сара ақын", "Сара ақын", "Sara the Poet"), ("Жамбыл-жас ақын", "Жас Жамбыл", "Young Zhambyl"),
                            ("Айнұр ақын", "Айнұр ақын", "Ainur the Poet"), ("Нұрлан ақын", "Нұрлан ақын", "Nurlan the Poet")])
many("aitys.ezhelgi.verse", [
    ("Я в степи родился, где ветер поёт, / Мой конь быстрее, чем время идёт!", "Мен даланың ұлымын, жел ән салған, / Тұлпарым уақыттан озып алған!",
     "I was born in the steppe where the wind sings, / My horse runs faster than time itself!"),
    ("Хвалишь коня, а сам ходишь пешком, / Слово твоё — как пустой казан с котлом!", "Атыңды мақтайсың, жаяу жүресің, / Сөзің бос қазандай — несін көресің!",
     "You praise your horse but walk on foot, / Your words are an empty cauldron!"),
    ("Мой дастархан широк, как Сарыарка, / Гость у меня — как солнце в юрте ярко!", "Дастарханым Сарыарқадай кең, / Қонағым — үйдегі күн, бәрінен тең!",
     "My table is wide as Saryarka, / A guest in my yurt shines like the sun!"),
    ("Дастархан широк, да баурсак мал, / Гость твой голодным домой ускакал!", "Дастарханың кең, бауырсағың аз, / Қонағың аш кетті, сөзің — тек наз!",
     "Your table is wide but the baursaks few, / Your guest went home hungry!"),
    ("Слово моё — как домбры струна: / Тронешь — и песня по степи слышна!", "Сөзім менің — домбыраның ішегі, / Шертсең болды — далаға ән түседі!",
     "My word is like a dombyra string: / Touch it, and song rings across the steppe!"),
    ("Струны твои давно не настроены, / Песни твои у соседей присвоены!", "Ішегің баяғыдан бері бұзық, / Әнің де көршіден алған, ұзын-сызық!",
     "Your strings have long been out of tune, / And your songs are borrowed from the neighbours!"),
    ("Я, как беркут, высоко лечу, / Слабых не трогаю — с равным шучу!", "Мен бүркіттей биікте ұшамын, / Әлсізді емес, теңімді құшамын!",
     "Like an eagle I fly up high, / I don't mock the weak — I joke with my equals!"),
    ("Беркут? Скорее воробей у плетня, / Перья распушил — да не видно огня!", "Бүркіт пе? Шарбақтағы торғайсың, / Қауырсын үрпиткен — жалын жоқ, ойлайсың!",
     "An eagle? More like a sparrow by the fence, / Feathers fluffed but no fire inside!"),
    ("Абай завещал: учись, не ленись, / Знанием, юноша, в жизни гордись!", "Абай айтқан: оқы, ерінбе, / Біліммен мақтан, басқамен емес, ұмытпа!",
     "Abai said: learn, don't be lazy, / Take pride, young one, in knowledge!"),
    ("Абая ты вспомнил — и это хорошо, / Но сам-то ты в книгу заглядывал ещё?", "Абайды еске алдың — жақсы-ақ, / Бірақ өзің кітап ашқан ба едің шынымен-ақ?",
     "You recall Abai — that's good, / But have you ever opened a book yourself?"),
    ("Весной расцветёт тюльпанами степь, / А я расцветаю, когда песню петь!", "Көктемде дала қызғалдақпен гүлдейді, / Мен ән айтсам — көңілім гүлдейді!",
     "In spring the steppe blooms with tulips, / And I bloom when I sing!"),
    ("Тюльпан расцветёт — и увянет к июню, / А слава твоя — как туман поутру лишь!", "Қызғалдақ гүлдейді де, маусымда солады, / Даңқың да таңғы тұмандай жоғалады!",
     "A tulip blooms and wilts by June, / Your fame fades like morning mist!"),
    ("Спор наш — не ссора, а праздник для всех, / Пусть в ауле звучит добрый смех!", "Айтысымыз — жанжал емес, той бәріне, / Ауылда күлкі шалқысын әрдайым қайта!",
     "Our contest is no quarrel but a feast for all, / Let kind laughter ring through the aul!"),
    ("Пусть победит тот, кто сердцем богат, / А проигравший — всё равно мне брат!", "Жүрегі бай жеңсін бұл айтыста, / Жеңілген де — бауырым, ренжіме, достым!",
     "Let the one rich in heart win, / And the loser is still my brother!"),
])
many("aul.ezhelgi.kokpar", [
    ("Көкпар! Джигиты, по коням! Поле — на %s от аула.", "Көкпар! Жігіттер, атқа қон! Алаң — ауылдың %s жағында.",
     "Kokpar! Riders, mount up! The field is to the %s of the aul."),
    ("Красные везут серке к красному флагу, синие — к синему.", "Қызылдар серкені қызыл туға, көктер — көк туға апарады.",
     "Reds carry the serke to the red flag, blues to the blue flag."),
    ("Хочешь играть — садись на коня и хватай серке (ПКМ). Ты за синих!", "Ойнағың келсе — атқа мін де, серкені ал (ТЖБ). Сен — көктерденсің!",
     "Want to play? Mount a horse and grab the serke (RMB). You play for the blues!"),
    ("Кто первым трижды донесёт серке — тот победил!", "Серкені үш рет бірінші жеткізген жеңеді!", "First to deliver the serke three times wins!"),
])
add("aul.ezhelgi.kokpar.need_horse", "В көкпар играют только верхом!", "Көкпарды тек атпен ойнайды!", "Kokpar is played only on horseback!")
add("aul.ezhelgi.kokpar.miss", "Не удалось вырвать серке!", "Серкені жұлып ала алмадың!", "You failed to snatch the serke!")
add("aul.ezhelgi.kokpar.grab", "Серке у тебя! Скачи к синему флагу!", "Серке сенде! Көк туға шап!", "You have the serke! Ride to the blue flag!")
add("aul.ezhelgi.kokpar.goal_red", "Красные забросили серке! Счёт: красные %s — синие %s", "Қызылдар серкені тастады! Есеп: %s — %s",
    "Reds score! Score: reds %s — blues %s")
add("aul.ezhelgi.kokpar.goal_blue", "Синие забросили серке! Счёт: красные %s — синие %s", "Көктер серкені тастады! Есеп: %s — %s",
    "Blues score! Score: reds %s — blues %s")
add("aul.ezhelgi.kokpar.win_blue", "Синие победили в көкпаре (%s — %s)! Победителям — награда!", "Көкпарда көктер жеңді (%s — %s)! Жеңімпаздарға сый!",
    "The blues won the kokpar (%s — %s)! Rewards for the winners!")
add("aul.ezhelgi.kokpar.win_red", "Красные победили в көкпаре (%s — %s). В следующий раз повезёт!", "Көкпарда қызылдар жеңді (%s — %s). Келесіде бағың жанар!",
    "The reds won the kokpar (%s — %s). Better luck next time!")
add("aul.ezhelgi.kokpar.draw", "Көкпар закончился вничью (%s — %s).", "Көкпар тең аяқталды (%s — %s).", "The kokpar ended in a draw (%s — %s).")
many("aul.ezhelgi.baige", [
    ("Бәйге! Скачки вокруг аула. Старт у флага на %s, через полминуты!", "Бәйге! Ауылды айнала шабамыз. Старт — %s жақтағы ту жанында, жарты минуттан соң!",
     "Baige! A horse race around the aul. Start by the flag to the %s in half a minute!"),
    ("Нужно объехать все четыре флага по кругу и вернуться к старту.", "Төрт туды айнала өтіп, стартқа қайту керек.",
     "Ride around all four flags in a circle and return to the start."),
    ("Хочешь участвовать — будь верхом у старта. Победителю — породистый конь!", "Қатысқың келсе — стартта атпен тұр. Жеңімпазға — тұқымды ат!",
     "Want to race? Be on horseback at the start. The winner gets a fine horse!"),
])
add("aul.ezhelgi.baige.ready", "Наездники, на старт!", "Шабандоздар, сапқа тұр!", "Riders, to the start!")
add("aul.ezhelgi.baige.go", "Пошли! Бәйге началась!", "Кеттік! Бәйге басталды!", "Go! The baige has begun!")
add("aul.ezhelgi.baige.mark", "Веха %s из %s", "%s / %s белгі", "Marker %s of %s")
add("aul.ezhelgi.baige.win_player", "%s выиграл бәйге! Приз — породистый конь под седлом и сакское золото!",
    "%s бәйгеде озды! Сыйлық — ерттеулі тұқымды ат пен сақ алтыны!", "%s won the baige! The prize is a fine saddled horse and Saka gold!")
add("aul.ezhelgi.baige.win_jockey", "Первым пришёл юный наездник аула! Участникам — утешительный приз.",
    "Ауылдың жас шабандозы озды! Қатысушыларға жұбаныш сыйлығы.", "A young rider of the aul came first! Consolation prizes for the racers.")
add("aul.ezhelgi.baige.prize_name", "Бәйге жүйрігі", "Бәйге жүйрігі", "Baige Champion")

# ------------------------------------------------------------------ жыр
add("zhyr.ezhelgi.next", "(Часть %s из %s. Приходи ещё — расскажу дальше.)", "(%s / %s бөлім. Тағы кел — әрі қарай айтамын.)",
    "(Part %s of %s. Come back and I'll tell you more.)")
add("zhyr.ezhelgi.all_heard", "Все жыры я тебе уже рассказал, балам. Послушай лучше старую мудрость.",
    "Барлық жырды саған айтып болдым, балам. Енді бір даналық тыңда.", "I've told you all the epics, child. Hear an old wisdom instead.")
add("zhyr.ezhelgi.no_map", "Сам не знаю, где теперь это место… Ищи в далёких краях.", "Ол жер қайда екенін өзім де білмеймін… Алыстан ізде.",
    "I myself don't know where that place is now... Search far away.")
EPICS = {
    "qobylandy": (("Жыр «Қобыланды батыр»", "«Қобыланды батыр» жыры", "The Epic of Qobylandy Batyr"), [
        [("Слушай, балам, жыр о Қобыланды из рода қыпшақ.", "Тыңда, балам, қыпшақ Қобыланды туралы жырды.", "Listen, child, to the epic of Qobylandy of the Kipchaks."),
         ("Жена его, мудрая Құртқа, вырастила жеребёнка Тайбурыла.", "Жары дана Құртқа Тайбурыл құлынын баптап өсірді.", "His wife, wise Qurtqa, raised the colt Taiburyl."),
         ("«Этот конь — твоя судьба», — сказала она мужу.", "«Бұл ат — сенің тағдырың», — деді ол.", "\"This horse is your destiny,\" she told her husband.")],
        [("Враг Қазан-хан напал на земли народа.", "Жау Қазан хан елге шапқыншылық жасады.", "The enemy Qazan Khan attacked the people's lands."),
         ("Қобыланды надел доспех и вскочил на Тайбурыла.", "Қобыланды сауытын киіп, Тайбурылға мінді.", "Qobylandy put on his armour and mounted Taiburyl."),
         ("Тайбурыл скакал так, что ветер не мог его догнать.", "Тайбурыл желден де озып шапты.", "Taiburyl galloped so fast the wind could not catch him.")],
        [("У стен вражеской крепости батыр бился много дней.", "Жау қамалының түбінде батыр көп күн шайқасты.", "At the walls of the enemy fortress the hero fought for many days."),
         ("Дочь врага, Қарлыға, увидела его отвагу и стала ему другом.", "Жаудың қызы Қарлыға оның ерлігін көріп, досы болды.",
          "The enemy's daughter Qarlygha saw his courage and became his friend."),
         ("Вместе они одолели Қазан-хана.", "Бірге Қазан ханды жеңді.", "Together they defeated Qazan Khan.")],
        [("Қобыланды вернулся домой с победой.", "Қобыланды елге жеңіспен оралды.", "Qobylandy returned home in victory."),
         ("Народ встретил его песнями, а Құртқа — улыбкой.", "Халық оны әнмен, Құртқа күлкімен қарсы алды.", "The people met him with songs, and Qurtqa with a smile."),
         ("С тех пор говорят: где Қобыланды — там народ спокоен.", "Содан бері: «Қобыланды бар жерде ел тыныш», — дейді.",
          "Since then they say: where Qobylandy is, the people are at peace.")],
    ], ("Говорят, доспех Қобыланды унёс айдахар в своё логово на горных вершинах. Вот карта — ищи там!",
        "Қобыландының сауытын айдаһар тау шыңындағы апанына алып кетіпті. Міне, карта — сонда ізде!",
        "They say the aidahar carried Qobylandy's armour to its lair on the mountain peaks. Here is a map — search there!"),
     ("Карта: логово айдахара", "Карта: айдаһар апаны", "Map: the Aidahar's Lair")),
    "alpamys": (("Жыр «Алпамыс батыр»", "«Алпамыс батыр» жыры", "The Epic of Alpamys Batyr"), [
        [("Долго у старого Байбөрі не было сына.", "Байбөрі қарттың ұзақ уақыт ұлы болмады.", "For a long time old Baibori had no son."),
         ("Наконец родился Алпамыс — сильный, как гора.", "Ақыры таудай мықты Алпамыс дүниеге келді.", "At last Alpamys was born — strong as a mountain."),
         ("С детства он обещан был красавице Гүлбаршын.", "Ол жастайынан сұлу Гүлбаршынға атастырылған еді.", "From childhood he was betrothed to the beautiful Gulbarshyn.")],
        [("Отец Гүлбаршын откочевал в дальние чужие земли.", "Гүлбаршынның әкесі алыс жат елге көшіп кетті.", "Gulbarshyn's father migrated to distant foreign lands."),
         ("Алпамыс сел на коня Байшұбар и поехал за невестой.", "Алпамыс Байшұбарға мініп, қалыңдығына аттанды.", "Alpamys mounted Baishubar and rode after his bride."),
         ("Он победил всех соперников и увёз Гүлбаршын домой.", "Барлық бәсекелесін жеңіп, Гүлбаршынды еліне алып қайтты.",
          "He defeated every rival and brought Gulbarshyn home.")],
        [("Но враг хитростью усыпил батыра и бросил в глубокую темницу.", "Бірақ жау айламен батырды ұйықтатып, терең зынданға тастады.",
          "But an enemy tricked the hero to sleep and threw him into a deep pit."),
         ("Ни огонь, ни стрелы не могли его убить — таким он был.", "Оны от та, оқ та ала алмады — сондай еді ол.", "Neither fire nor arrows could kill him — such was he."),
         ("Долгие годы он ждал и не терял надежды.", "Ұзақ жылдар күтті, үмітін үзбеді.", "For long years he waited and never lost hope.")],
        [("Верный Байшұбар помог ему выбраться на свободу.", "Адал Байшұбар оны бостандыққа шығуға көмектесті.", "Faithful Baishubar helped him escape."),
         ("Алпамыс вернулся домой и прогнал обидчиков своей семьи.", "Алпамыс елге оралып, отбасын қорлағандарды қуды.", "Alpamys returned home and drove off those who wronged his family."),
         ("И снова в его доме зазвучала радость.", "Үйіне қайта қуаныш оралды.", "And joy rang again in his home.")],
    ], ("Доспех Алпамыса, говорят, спрятан в тайнике мавзолея Ясави. Вот карта — ищи под коврами библиотеки!",
        "Алпамыстың сауыты Ясауи кесенесінің құпиясында жасырылған дейді. Міне, карта — кітапхана кілемдерінің астын қара!",
        "Alpamys's armour is said to be hidden in the secret cache of the Yasawi mausoleum. Here is a map — look under the library rugs!"),
     ("Карта: мавзолей Ясави", "Карта: Ясауи кесенесі", "Map: the Yasawi Mausoleum")),
    "er_targyn": (("Жыр «Ер Тарғын»", "«Ер Тарғын» жыры", "The Epic of Er Targyn"), [
        [("Тарғын был юным и храбрым джигитом.", "Тарғын жас әрі батыл жігіт еді.", "Targyn was a young and brave rider."),
         ("Он не терпел несправедливости и ушёл искать свою долю.", "Әділетсіздікке шыдамай, бағын іздеп кетті.", "He could not bear injustice and went to seek his fortune."),
         ("Конь его был быстрее степного ветра.", "Аты дала желінен жүйрік еді.", "His horse was swifter than the steppe wind.")],
        [("В пути он встретил прекрасную Ақжүніс.", "Жолда сұлу Ақжүніспен кездесті.", "On the way he met the beautiful Aqzhunis."),
         ("Они полюбили друг друга и умчались вместе.", "Олар бір-бірін ұнатып, бірге кетті.", "They fell in love and rode away together."),
         ("За ними пустился в погоню старый воин Қартқожақ.", "Олардың соңынан кәрі батыр Қартқожақ қуды.", "The old warrior Qartqozhaq set off in pursuit.")],
        [("Догнав их, Қартқожақ увидел отвагу юноши.", "Қуып жеткен Қартқожақ жігіттің ерлігін көрді.", "Catching up, Qartqozhaq saw the young man's courage."),
         ("«Такого батыра не губят — таких берегут», — сказал он.", "«Мұндай батырды өлтірмейді — сақтайды», — деді ол.",
          "\"Such a hero is not destroyed — he is protected,\" he said."),
         ("И отпустил их с благословением.", "Сөйтіп, батасын беріп жіберді.", "And let them go with his blessing.")],
        [("Позже враги напали на народ, и Тарғын встал на защиту.", "Кейін ел жауға тап болды, Тарғын қорғауға шықты.", "Later enemies attacked the people, and Targyn rose to defend them."),
         ("Раненого батыра выходила верная Ақжүніс.", "Жараланған батырды адал Ақжүніс емдеп жазды.", "Faithful Aqzhunis nursed the wounded hero back to health."),
         ("Так любовь и мужество победили беду.", "Сөйтіп, махаббат пен ерлік бәлені жеңді.", "So love and courage overcame misfortune.")],
    ], ("Доспех Ер Тарғына, говорят, лежит среди скал Тамгалы, где древние рисовали солнцеголовых. Вот карта!",
        "Ер Тарғынның сауыты Тамғалы жартастарының арасында жатыр дейді. Міне, карта!",
        "Er Targyn's armour is said to lie among the Tamgaly rocks, where the ancients drew the sun-headed ones. Here is a map!"),
     ("Карта: скалы Тамгалы", "Карта: Тамғалы жартастары", "Map: the Tamgaly Rocks")),
}
for epic, (title, parts, hint, mapname) in EPICS.items():
    add(f"zhyr.ezhelgi.{epic}.title", *title)
    for p, lines in enumerate(parts):
        for i, l in enumerate(lines):
            add(f"zhyr.ezhelgi.{epic}.{p}.{i}", *l)
    add(f"zhyr.ezhelgi.{epic}.hint", *hint)
    add(f"zhyr.ezhelgi.{epic}.map", *mapname)


def names():
    return dict(N)
