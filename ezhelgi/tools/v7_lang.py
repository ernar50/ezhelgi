"""v7: тексты (ru_ru, kk_kz, en_us)."""

N = {}


def add(key, ru, kk, en):
    N[key] = (ru, kk, en)


# ------------------------------------------------------------------ доспехи
ARMOR = {
    "dulyga": ("Дулыға (шлем батыра)", "Дулыға", "Dulyga (Batyr Helmet)"),
    "kireuke": ("Кіреуке (кольчужная рубаха)", "Кіреуке", "Kireuke (Mail Shirt)"),
    "kobe_sauyt": ("Көбе сауыт (длинная кольчуга)", "Көбе сауыт", "Kobe Sauyt (Long Mail)"),
    "beren_sauyt": ("Берен сауыт (булатный доспех)", "Берен сауыт", "Beren Sauyt (Damask Armour)"),
    "sharaina": ("Шарайна (зерцальный доспех)", "Шарайна", "Sharaina (Mirror Armour)"),
    "batyr_shalbar": ("Шалбар батыра", "Батыр шалбары", "Batyr Trousers"),
    "batyr_etik": ("Етік батыра", "Батыр етігі", "Batyr Boots"),
    "jungar_dulyga": ("Джунгарский шлем", "Жоңғар дулығасы", "Jungar Helmet"),
    "jungar_sauyt": ("Джунгарский ламеллярный доспех", "Жоңғар сауыты", "Jungar Lamellar Armour"),
    "jungar_shalbar": ("Джунгарские поножи", "Жоңғар шалбары", "Jungar Leggings"),
    "jungar_etik": ("Джунгарские сапоги", "Жоңғар етігі", "Jungar Boots"),
    "tymaq": ("Тымақ (лисья шапка)", "Түлкі тымақ", "Tymaq (Fox-Fur Hat)"),
}
for k, v in ARMOR.items():
    add(f"item.ezhelgi.{k}", *v)
HEROES = {
    "qobylandy": ("Қобыланды батыра", "Қобыланды батырдың", "of Qobylandy Batyr"),
    "alpamys": ("Алпамыса батыра", "Алпамыс батырдың", "of Alpamys Batyr"),
    "er_targyn": ("Ер Тарғына", "Ер Тарғынның", "of Er Targyn"),
}
PARTS = {
    "dulyga": ("Шлем", "дулығасы", "Helmet"),
    "sauyt": ("Доспех", "сауыты", "Armour"),
    "shalbar": ("Поножи", "шалбары", "Leggings"),
    "etik": ("Сапоги", "етігі", "Boots"),
}
for h, (hr, hk, he) in HEROES.items():
    for p, (pr, pk, pe) in PARTS.items():
        add(f"item.ezhelgi.{h}_{p}", f"{pr} {hr}", f"{hk} {pk}", f"{pe} {he}")
add("tooltip.ezhelgi.set.full", "Полный набор:", "Толық жиынтық:", "Full set:")
add("tooltip.ezhelgi.set.qobylandy", "Доспех героя эпоса «Қобыланды батыр»",
    "«Қобыланды батыр» жырының батырының сауыты", "Armour of the hero of the epic \"Qobylandy Batyr\"")
add("tooltip.ezhelgi.set.qobylandy.bonus", "Сила и стойкость; твой конь скачет как Тайбурыл",
    "Күш пен төзім; атың Тайбурылдай шабады", "Strength and resistance; your horse runs like Taiburyl")
add("tooltip.ezhelgi.set.alpamys", "Доспех Алпамыса, которого не брали ни огонь, ни стрелы",
    "Отқа да, оққа да шыдаған Алпамыстың сауыты", "Armour of Alpamys, whom neither fire nor arrows could harm")
add("tooltip.ezhelgi.set.alpamys.bonus", "Огнестойкость и сильная защита", "Отқа төзімділік пен мықты қорған",
    "Fire resistance and strong protection")
add("tooltip.ezhelgi.set.er_targyn", "Доспех быстрого и ловкого Ер Тарғына", "Ұшқыр да епті Ер Тарғынның сауыты",
    "Armour of swift and nimble Er Targyn")
add("tooltip.ezhelgi.set.er_targyn.bonus", "Скорость, прыжок и сноровка", "Жылдамдық, секіру және епшілдік",
    "Speed, jump boost and haste")
add("item.ezhelgi.qalqan", "Қалқан (щит батыра)", "Қалқан", "Qalqan (Batyr Shield)")
add("item.ezhelgi.aidahar_qalqany", "Щит из чешуи айдахара", "Айдаһар қалқаны", "Aidahar Scale Shield")

# ------------------------------------------------------------------ отделка
TRIMS = {
    "qoshqar_muiz": ("Қошқар мүйіз (бараньи рога)", "Қошқар мүйіз", "Qoshqar Muiz (Ram's Horns)"),
    "tuie_taban": ("Түйе табан (след верблюда)", "Түйе табан", "Tuie Taban (Camel Footprint)"),
    "qus_qanaty": ("Құс қанаты (крылья птицы)", "Құс қанаты", "Qus Qanaty (Bird Wings)"),
    "synar_muiz": ("Сыңар мүйіз (одиночный рог)", "Сыңар мүйіз", "Synar Muiz (Single Horn)"),
    "tumarsha": ("Тұмарша (обереги)", "Тұмарша", "Tumarsha (Amulets)"),
}
for k, (ru, kk, en) in TRIMS.items():
    add(f"trim_pattern.ezhelgi.{k}", f"Узор: {ru}", f"Ою: {kk}", f"{en} Trim")
    add(f"item.ezhelgi.{k}_armor_trim_smithing_template", f"Шаблон отделки: {ru}", f"Әшекей үлгісі: {kk}", f"{en} Smithing Template")

# ------------------------------------------------------------------ предметы
add("item.ezhelgi.music_disc_adai", "Пластинка", "Пластинка", "Music Disc")
add("item.ezhelgi.music_disc_adai.desc", "Құрманғазы — Адай", "Құрманғазы — Адай", "Kurmangazy — Adai")
add("jukebox_song.ezhelgi.adai", "Құрманғазы — Адай", "Құрманғазы — Адай", "Kurmangazy — Adai")
add("item.ezhelgi.aidahar_qabyrshagy", "Чешуя айдахара", "Айдаһар қабыршағы", "Aidahar Scale")
add("item.ezhelgi.arqar_muiizi", "Рог архара", "Арқар мүйізі", "Argali Horn")
add("item.ezhelgi.muiiz_sadaq", "Роговой лук", "Мүйіз садақ", "Horn Bow")
add("item.ezhelgi.tulki_terisi", "Лисья шкура", "Түлкі терісі", "Fox Pelt")
add("item.ezhelgi.an_eti", "Сырая дичь", "Шикі аң еті", "Raw Game")
add("item.ezhelgi.pisken_an_eti", "Жареная дичь", "Піскен аң еті", "Cooked Game")
add("item.ezhelgi.qumalaq", "Құмалақ", "Құмалақ", "Qumalaq Stone")
add("item.ezhelgi.qumalaq_tuzdyk", "Метка тұздық", "Тұздық белгісі", "Tuzdyk Marker")
add("item.ezhelgi.oiu_banner_pattern", "Трафарет «Ою-өрнек»", "Ою-өрнек үлгісі", "Oiu-Ornek Banner Pattern")
add("block.ezhelgi.togyz_qumalaq", "Тоғыз құмалақ", "Тоғыз құмалақ", "Togyz Qumalaq Board")
add("block.ezhelgi.tu_qazaqstan", "Флаг Казахстана", "Қазақстан туы", "Flag of Kazakhstan")
add("block.ezhelgi.tu_alash", "Флаг Алаш (проект)", "Алаш туы (жоба)", "Alash Flag (proposed)")
add("block.ezhelgi.tu_handyq", "Знамя Казахского ханства (стилизация)", "Қазақ хандығының туы (стилизация)",
    "Kazakh Khanate Banner (stylised)")
add("block.ezhelgi.tu_uly_juz", "Знамя Старшего жуза (стилизация)", "Ұлы жүз туы (стилизация)", "Senior Zhuz Banner (stylised)")
add("block.ezhelgi.tu_orta_juz", "Знамя Среднего жуза (стилизация)", "Орта жүз туы (стилизация)", "Middle Zhuz Banner (stylised)")
add("block.ezhelgi.tu_kishi_juz", "Знамя Младшего жуза (стилизация)", "Кіші жүз туы (стилизация)", "Junior Zhuz Banner (stylised)")
add("block.ezhelgi.tanba_tas", "Таңба тас (петроглиф)", "Таңба тас", "Petroglyph Rock")
add("block.ezhelgi.an_qum", "Ән құм (поющий песок)", "Ән құм", "Singing Sand")

# готовые знамёна во вкладке
add("banner.ezhelgi.qazaqstan", "Знамя Казахстана", "Қазақстан байрағы", "Banner of Kazakhstan")
add("banner.ezhelgi.alash", "Знамя Алаш", "Алаш байрағы", "Alash Banner")
add("banner.ezhelgi.handyq", "Знамя Казахского ханства", "Қазақ хандығы байрағы", "Kazakh Khanate Banner")
add("banner.ezhelgi.uly_juz", "Знамя Старшего жуза", "Ұлы жүз байрағы", "Senior Zhuz Banner")
add("banner.ezhelgi.orta_juz", "Знамя Среднего жуза", "Орта жүз байрағы", "Middle Zhuz Banner")
add("banner.ezhelgi.kishi_juz", "Знамя Младшего жуза", "Кіші жүз байрағы", "Junior Zhuz Banner")

COLORS = {
    "white": ("Белый", "Ақ", "White"), "orange": ("Оранжевый", "Қызғылт сары", "Orange"),
    "magenta": ("Сиреневый", "Күлгін", "Magenta"), "light_blue": ("Голубой", "Көгілдір", "Light Blue"),
    "yellow": ("Жёлтый", "Сары", "Yellow"), "lime": ("Лаймовый", "Ашық жасыл", "Lime"), "pink": ("Розовый", "Қызғылт", "Pink"),
    "gray": ("Серый", "Сұр", "Gray"), "light_gray": ("Светло-серый", "Ашық сұр", "Light Gray"), "cyan": ("Бирюзовый", "Көкшіл", "Cyan"),
    "purple": ("Фиолетовый", "Шырай", "Purple"), "blue": ("Синий", "Көк", "Blue"), "brown": ("Коричневый", "Қоңыр", "Brown"),
    "green": ("Зелёный", "Жасыл", "Green"), "red": ("Красный", "Қызыл", "Red"), "black": ("Чёрный", "Қара", "Black"),
}
PATTERNS = {
    "kz_kun": ("солнце с 32 лучами", "32 шұғылалы күн", "Sun with 32 Rays"),
    "kz_qyran": ("степной орёл", "қыран", "Steppe Eagle"),
    "kz_oiu": ("полоса қошқар мүйіз", "қошқар мүйіз жолағы", "Ram's Horn Band"),
    "alash_ai": ("полумесяц", "ай", "Crescent"),
    "alash_juldyz": ("звезда", "жұлдыз", "Star"),
    "shanyraq": ("шаңырақ", "шаңырақ", "Shanyraq"),
    "tamga_uly": ("тамга Старшего жуза", "Ұлы жүз таңбасы", "Senior Zhuz Tamga"),
    "tamga_orta": ("тамга Среднего жуза", "Орта жүз таңбасы", "Middle Zhuz Tamga"),
    "tamga_kishi": ("тамга Младшего жуза", "Кіші жүз таңбасы", "Junior Zhuz Tamga"),
    "qoshqar_muiz": ("кайма қошқар мүйіз", "қошқар мүйіз жиегі", "Ram's Horn Border"),
}
for p, (pr, pk, pe) in PATTERNS.items():
    for col, (cr, ck, ce) in COLORS.items():
        add(f"block.ezhelgi.banner.{p}.{col}", f"{cr}: {pr}", f"{ck} {pk}", f"{ce} {pe}")

# ------------------------------------------------------------------ существа
ENT = {
    "arqar": ("Архар", "Арқар", "Argali"),
    "qaraquiryq": ("Джейран", "Қарақұйрық", "Goitered Gazelle"),
    "tau_teke": ("Тау-теке (сибирский козёл)", "Тау теке", "Siberian Ibex"),
    "ayu": ("Тянь-шаньский бурый медведь", "Тянь-Шань аюы", "Tian Shan Brown Bear"),
    "manul": ("Манул", "Манул", "Pallas's Cat"),
    "qarsaq": ("Корсак", "Қарсақ", "Corsac Fox"),
    "itbalyq": ("Каспийский тюлень", "Каспий итбалығы", "Caspian Seal"),
    "qosayaq": ("Тушканчик", "Қосаяқ", "Jerboa"),
    "przewalski": ("Лошадь Пржевальского", "Пржевальский жылқысы", "Przewalski's Horse"),
    "qaraqus": ("Қарақұс (гигантский стервятник)", "Қарақұс", "Qaraqus (Giant Vulture)"),
    "aidahar": ("Айдаһар", "Айдаһар", "Aidahar the Dragon"),
    "mystan_kempir": ("Мыстан кемпір", "Мыстан кемпір", "Mystan Kempir"),
    "mystan_tulki": ("Мыстан-лиса", "Мыстан түлкі", "Mystan Fox"),
    "aqsaqal": ("Ақсақал (старейшина)", "Ақсақал", "Aqsaqal (Elder)"),
    "kuishi": ("Күйші (музыкант)", "Күйші", "Kuishi (Musician)"),
    "keruenshi": ("Керуенші (караванщик)", "Керуенші", "Keruenshi (Caravan Trader)"),
}
for k, (ru, kk, en) in ENT.items():
    add(f"entity.ezhelgi.{k}", ru, kk, en)
    if k != "mystan_tulki":
        add(f"item.ezhelgi.{k}_spawn_egg", f"Яйцо призыва: {ru}", f"{kk} жұмыртқасы", f"{en} Spawn Egg")

# ------------------------------------------------------------------ звуки
for k, v in {
    "aidahar_roar": ("Рёв айдахара", "Айдаһар ақырады", "Aidahar roars"),
    "aidahar_bite": ("Айдахар кусает", "Айдаһар тістейді", "Aidahar bites"),
    "aidahar_fire": ("Айдахар дышит огнём", "Айдаһар от бүркеді", "Aidahar breathes fire"),
    "aidahar": ("Айдахар рычит", "Айдаһар ырылдайды", "Aidahar growls"),
    "aqsaqal": ("Аксакал говорит", "Ақсақал сөйлейді", "Aqsaqal speaks"),
    "togyz": ("Стучат құмалақ", "Құмалақ тықылдайды", "Qumalaq clatter"),
    "an_qum": ("Гудит бархан", "Бархан ән салады", "Dune hums"),
    "tanba": ("Найден петроглиф", "Таңба табылды", "Petroglyph found"),
}.items():
    add(f"subtitles.ezhelgi.{k}", *v)

# ------------------------------------------------------------------ тоғыз құмалақ
for k, v in {
    "over": ("Партия окончена. Shift+ПКМ — новая", "Ойын бітті. Shift+ТЖБ — жаңасы", "Game over. Shift+RMB for a new one"),
    "aksakal_row": ("Это лунки аксакала — твой ряд ближний", "Бұл ақсақалдың отаулары — сенікі жақын қатар",
                    "These are the elder's pits — yours is the near row"),
    "wait": ("Сейчас не твой ход", "Қазір сенің кезегің емес", "It's not your turn"),
    "empty": ("В этой лунке нет құмалақ", "Бұл отауда құмалақ жоқ", "This pit is empty"),
    "side0": ("Ближний игрок", "Жақын ойыншы", "Near player"),
    "side1": ("Дальний игрок", "Алыс ойыншы", "Far player"),
    "aksakal": ("Аксакал", "Ақсақал", "The elder"),
    "tuzdyk": ("%s объявляет тұздық!", "%s тұздық алды!", "%s declares a tuzdyk!"),
    "draw": ("Ничья!", "Тең!", "Draw!"),
    "win": ("%s побеждает!", "%s жеңді!", "%s wins!"),
    "final": ("%s Счёт: %s — %s", "%s Есеп: %s — %s", "%s Score: %s — %s"),
    "score": ("Қазан: ты %s — соперник %s. Ходит: %s", "Қазан: сен %s — қарсылас %s. Кезек: %s",
              "Kazan: you %s — rival %s. Turn: %s"),
    "new_ai": ("Новая партия против аксакала", "Ақсақалмен жаңа ойын", "New game against the elder"),
    "new_pvp": ("Новая партия на двоих", "Екі адамға жаңа ойын", "New two-player game"),
}.items():
    add(f"message.ezhelgi.togyz.{k}", *v)

# ------------------------------------------------------------------ петроглифы
add("message.ezhelgi.tanba.known", "Ты уже видел этот рисунок: %s", "Бұл таңбаны көргенсің: %s", "You have seen this one: %s")
add("message.ezhelgi.tanba.found", "Найден петроглиф «%s» (%s из %s)", "«%s» таңбасы табылды (%s / %s)",
    "Petroglyph found: \"%s\" (%s of %s)")
add("message.ezhelgi.tanba.all", "Ты нашёл все рисунки Тамгалы! Древние мастера дарят тебе награду.",
    "Тамғалының барлық таңбасын таптың! Ежелгі шеберлер саған сыйлық береді.",
    "You found every petroglyph of Tamgaly! The ancient masters reward you.")
GLYPHS = {
    "kunbasty": (("Солнцеголовый", "Күнбасты", "Sun-Headed One"),
                 ("Божество с головой-солнцем — самые известные рисунки Тамгалы, им больше 3000 лет.",
                  "Басы күн тәрізді құдай — Тамғалының ең әйгілі суреттері, 3000 жылдан асқан.",
                  "A deity with a sun for a head — Tamgaly's most famous carvings, over 3,000 years old.")),
    "biy": (("Хоровод", "Би", "Round Dance"),
            ("Люди, взявшиеся за руки, — древний праздник или обряд.",
             "Қол ұстасқан адамдар — ежелгі той немесе ғұрып.",
             "People holding hands — an ancient feast or ritual.")),
    "ogiz": (("Бык", "Өгіз", "Bull"),
             ("Бык — символ силы и богатства древних скотоводов.", "Өгіз — ежелгі малшылардың күші мен байлығының белгісі.",
              "The bull — a sign of the strength and wealth of ancient herders.")),
    "at": (("Конь", "Ат", "Horse"),
           ("Конь — крылья степняка. Без него не было бы кочевой жизни.", "Ат — ер қанаты. Онсыз көшпелі өмір болмас еді.",
            "The horse is the nomad's wings; without it there would be no nomadic life.")),
    "bugy": (("Олень", "Бұғы", "Deer"),
             ("Олень с ветвистыми рогами — любимый зверь сакских мастеров.", "Бұтақ мүйізді бұғы — сақ шеберлерінің сүйікті аңы.",
              "A deer with branching antlers — beloved by Saka craftsmen.")),
    "tuie": (("Верблюд", "Түйе", "Camel"),
             ("Двугорбый верблюд — корабль пустыни и Шёлкового пути.", "Қос өркешті түйе — шөл мен Жібек жолының кемесі.",
              "The Bactrian camel — ship of the desert and the Silk Road.")),
    "mergen": (("Лучник", "Мерген", "Archer"),
               ("Охотник с луком — мерген, который не промахивается.", "Садақты аңшы — мүлт кетпейтін мерген.",
                "A hunter with a bow — a marksman who never misses.")),
    "arba": (("Колесница", "Арба", "Chariot"),
             ("Колесница с двумя колёсами — память о древних кочевых путях.", "Қос дөңгелекті арба — ежелгі көш жолдарының белгісі.",
              "A two-wheeled chariot — memory of the ancient nomad roads.")),
}
for g, (names, desc) in GLYPHS.items():
    add(f"glyph.ezhelgi.{g}", *names)
    add(f"glyph.ezhelgi.{g}.desc", *desc)

# ------------------------------------------------------------------ аксакал
add("aqsaqal.ezhelgi.says", "<%s> %s", "<%s> %s", "<%s> %s")
NAMES = [("Төлеген ата", "Төлеген ата", "Tolegen Ata"), ("Жақып ата", "Жақып ата", "Zhaqyp Ata"),
         ("Сейіт ата", "Сейіт ата", "Seiit Ata"), ("Әбдірахман ата", "Әбдірахман ата", "Abdirakhman Ata"),
         ("Мұхтар ата", "Мұхтар ата", "Mukhtar Ata"), ("Бекет ата", "Бекет ата", "Beket Ata"),
         ("Ораз ата", "Ораз ата", "Oraz Ata"), ("Қасым ата", "Қасым ата", "Qasym Ata")]
for i, n in enumerate(NAMES):
    add(f"aqsaqal.ezhelgi.name.{i}", *n)
GREET = [
    ("Ассалаумағалейкум, балам! Подойди, присядь у огня.", "Ассалаумағалейкум, балам! Кел, отқа жақын отыр.",
     "Assalaumagaleikum, my child! Come, sit by the fire."),
    ("А, путник! Добро пожаловать в наш аул.", "Ә, жолаушы! Ауылымызға қош келдің.", "Ah, a traveller! Welcome to our aul."),
    ("Сәлем, батыр. Вижу, дорога твоя была долгой.", "Сәлем, батыр. Жолың ұзақ болғанын көріп тұрмын.",
     "Greetings, batyr. I see your road was long."),
    ("Иә, балам, хорошо, что ты пришёл. Есть дело.", "Иә, балам, келгенің жақсы болды. Шаруа бар.",
     "Yes, my child, good that you came. There is work."),
    ("Қош келдің! Выпей кумыса, потом поговорим.", "Қош келдің! Қымыз іш, сосын сөйлесейік.",
     "Welcome! Drink some kumys, then we shall talk."),
    ("Эх, годы мои... Хорошо, что молодые ещё заходят к старику.", "Әй, жасым-ай... Жастардың қартқа соғатыны жақсы.",
     "Ah, my years... It is good the young still visit an old man."),
]
for i, g in enumerate(GREET):
    add(f"aqsaqal.ezhelgi.greet.{i}", *g)
WISDOM = [
    ("«Жеті атасын білмеген — жетесіз». Знай семь поколений предков, балам.",
     "Жеті атасын білмеген — жетесіз. Ата-тегіңді біл, балам.",
     "\"Who does not know seven generations of ancestors is rootless.\" Know your forebears, child."),
    ("«Ат — ер қанаты». Береги коня, и он вынесет тебя из любой беды.", "Ат — ер қанаты. Атыңды күт, ол сені кез келген бәледен алып шығады.",
     "\"The horse is a man's wings.\" Care for your horse and it will carry you from any trouble."),
    ("Говорят, в горах Алатау снова видели айдахара. Пастухи боятся гнать туда отары.",
     "Алатау тауларында айдаһарды тағы көріпті. Шопандар отарын апаруға қорқады.",
     "They say the aidahar was seen in the Alatau again. Shepherds fear to drive their flocks there."),
    ("«Бірлік болмай, тірлік болмас». Без единства нет жизни — помни это.", "Бірлік болмай, тірлік болмас. Осыны есте сақта.",
     "\"Without unity there is no life.\" Remember that."),
    ("Мой дед говорил: кто рано встаёт, тому Тәңір даёт.", "Атам айтатын: ерте тұрған жігіттің ырысы артық.",
     "My grandfather said: the one who rises early receives the blessing."),
    ("Вчера кюйши играл «Адай» Құрманғазы — даже кони остановились послушать.",
     "Кеше күйші Құрманғазының «Адайын» тартты — аттар да тоқтап тыңдады.",
     "Yesterday the kuishi played Kurmangazy's \"Adai\" — even the horses stopped to listen."),
    ("«Өнер алды — қызыл тіл». Умей сказать слово вовремя, батыр.", "Өнер алды — қызыл тіл. Сөзді орнымен айта біл.",
     "\"The first of arts is the tongue.\" Know when to speak, batyr."),
    ("Абай учил: будь цельным человеком — ум, сердце и воля вместе.", "Абай: толық адам бол — ақыл, жүрек, қайрат бірге болсын.",
     "Abai taught: be a whole person — mind, heart and will together."),
    ("Ночью на кладбищах шевелятся тени. Не ходи туда без тұмара.", "Түнде зираттарда көлеңкелер қозғалады. Тұмарсыз барма.",
     "At night shadows stir in the graveyards. Don't go there without a tumar."),
    ("Сайгаков в этом году много. Значит, будет хорошая весна.", "Биыл ақбөкен көп. Демек, көктем жақсы болады.",
     "Many saiga this year. That means a good spring."),
    ("«Қонақ келсе, құт келер». Гость в дом — благо в дом.", "Қонақ келсе, құт келер.", "\"When a guest comes, blessing comes.\""),
    ("Караван с Шёлкового пути привёз шёлк и сахар. Торговцы говорят, в Отраре опять разбойники.",
     "Жібек жолынан келген керуен жібек пен қант әкелді. Саудагерлер Отырарда қарақшылар бар дейді.",
     "A Silk Road caravan brought silk and sugar. Traders say bandits are in Otrar again."),
    ("Тоғыз құмалақ учит думать на три хода вперёд. Как и сама жизнь.", "Тоғыз құмалақ үш жүріс алға ойлауға үйретеді. Өмір де солай.",
     "Togyz qumalaq teaches you to think three moves ahead. So does life."),
    ("«Жігітке жеті өнер де аз». Учись всему, что встретишь.", "Жігітке жеті өнер де аз.", "\"Even seven crafts are few for a young man.\" Learn everything."),
    ("Мыстан кемпір снова бродит у леса — превращается в рыжую лису. Не верь лисам на закате.",
     "Мыстан кемпір орман шетінде жүр — қызыл түлкіге айналады. Кешке түлкіге сенбе.",
     "Mystan kempir roams the forest edge again — she turns into a red fox. Trust no fox at dusk."),
    ("Аральское море уходит, балам. Там, где плавали лодки, теперь лежат ржавые корабли.",
     "Арал теңізі тартылып барады, балам. Қайық жүзген жерде енді тот басқан кемелер жатыр.",
     "The Aral Sea is leaving, child. Where boats once sailed, rusty ships now lie."),
    ("Мавзолей Ходжи Ахмеда Ясави построил Тимур. Говорят, в его библиотеке есть тайник.",
     "Қожа Ахмет Ясауи кесенесін Әмір Темір салдырған. Кітапханасында құпия бар дейді.",
     "Timur built the mausoleum of Khoja Ahmed Yasawi. They say its library hides a secret."),
    ("В Тамгалы на скалах нарисованы солнцеголовые боги. Найди их всех — предки будут довольны.",
     "Тамғалы жартастарында күнбасты құдайлар салынған. Бәрін тапсаң, аруақтар риза болады.",
     "In Tamgaly the rocks bear sun-headed gods. Find them all and the ancestors will be pleased."),
    ("Говорят, на бархане Ән құм поёт сама земля. Пройдись по нему — услышишь.",
     "Ән құм барханында жердің өзі ән салады дейді. Үстімен жүрсең, естисің.",
     "They say on the Singing Dune the earth itself sings. Walk on it and you'll hear."),
    ("«Еңбек етсең ерінбей, тояды қарның тіленбей». Трудись — и не будешь просить.",
     "Еңбек етсең ерінбей, тояды қарның тіленбей.", "\"Work without laziness and you won't need to beg.\""),
    ("Зима была суровой, джут унёс много скота. Нам нужна помощь каждого.",
     "Қыс қатты болды, жұт көп малды алып кетті. Әркімнің көмегі керек.",
     "Winter was harsh; the zhut took many animals. We need everyone's help."),
    ("Джунгары снова тревожат границы. Наши батыры точат клинки.", "Жоңғарлар шекараны тағы мазалап жүр. Батырларымыз қылыш қайрап отыр.",
     "The Jungars trouble the borders again. Our batyrs sharpen their blades."),
    ("Лошади Пржевальского — дикие кони наших степей. Мы бережём их в загоне, как сокровище.",
     "Пржевальский жылқылары — даламыздың жабайы аттары. Оларды қорада қазынадай сақтаймыз.",
     "Przewalski's horses are the wild horses of our steppe. We keep them in the pen like treasure."),
    ("«Ер жігіт елі үшін туады». Батыр рождается ради своего народа.", "Ер жігіт елі үшін туады, елі үшін өледі.",
     "\"A hero is born for his people.\""),
    ("Әл-Фараби называл музыку лекарством для души. Слушай домбыру почаще.",
     "Әл-Фараби музыканы жанның емі деген. Домбыраны жиі тыңда.",
     "Al-Farabi called music medicine for the soul. Listen to the dombyra often."),
    ("Қарақұс опять утащил ягнёнка. Огромная птица, крылья как кошма.",
     "Қарақұс тағы қозыны алып кетті. Алып құс, қанаты киіздей.",
     "The qaraqus carried off a lamb again. A huge bird, wings like a felt rug."),
    ("Шокан Уалиханов ездил до самого Кашгара. Учись, и ты увидишь мир.",
     "Шоқан Уәлиханов Қашқарға дейін барған. Оқысаң, әлемді көресің.",
     "Shoqan Ualikhanov rode all the way to Kashgar. Study and you will see the world."),
    ("Береги воду в степи, балам. Колодец — это жизнь.", "Далада суды қадірле, балам. Құдық — өмір.",
     "Treasure water in the steppe, child. A well is life."),
    ("«Сабыр түбі — сары алтын». Терпение — золото.", "Сабыр түбі — сары алтын.", "\"Patience is pure gold.\""),
    ("Внуки мои растут — скоро и они сядут в седло. Время летит, как конь Тайбурыл.",
     "Немерелерім өсіп келеді — жақында олар да ат жалын тартып мінеді. Уақыт Тайбурылдай зымырайды.",
     "My grandchildren grow — soon they too will ride. Time flies like the horse Taiburyl."),
]
assert len(WISDOM) == 30
for i, w in enumerate(WISDOM):
    add(f"aqsaqal.ezhelgi.wisdom.{i}", *w)
REMIND = [
    ("Ну что, балам, как продвигается дело?", "Ал, балам, шаруа қалай?", "Well, child, how goes the task?"),
    ("Не забыл ли ты мою просьбу?", "Өтінішімді ұмытпадың ба?", "Have you forgotten my request?"),
    ("Аул ждёт, батыр. Помни, о чём я просил.", "Ауыл күтіп отыр, батыр. Не сұрағанымды есте сақта.",
     "The aul is waiting, batyr. Remember what I asked."),
    ("Спешить не надо, но и медлить не стоит.", "Асықпа, бірақ кешікпе де.", "No need to hurry, but don't tarry either."),
]
for i, r in enumerate(REMIND):
    add(f"aqsaqal.ezhelgi.remind.{i}", *r)
BATA = [
    ("Жолың болсын! Пусть дорога твоя будет светлой.", "Жолың болсын! Жолың ашық болсын.", "May your road be bright!"),
    ("Бақытты бол, балам. Пусть удача не покидает тебя.", "Бақытты бол, балам. Бағың жансын.", "Be happy, child. May luck stay with you."),
    ("Ата-бабаларың разы болсын! Аруақтар довольны тобой.", "Аруақ риза болсын!", "May the ancestors' spirits be pleased with you!"),
    ("Пусть твой конь будет быстрым, а клинок — острым.", "Атың жүйрік, қылышың өткір болсын.", "May your horse be swift and your blade sharp."),
    ("Әумин! Пусть дом твой будет полон, как казан на тое.", "Әумин! Үйің тойдағы қазандай толы болсын.",
     "Aumin! May your home be full as a feast cauldron."),
]
for i, b in enumerate(BATA):
    add(f"aqsaqal.ezhelgi.bata.{i}", *b)
add("aqsaqal.ezhelgi.abandon", "Ладно, балам, не всякое дело по силам. Приходи — найду другое.",
    "Жарайды, балам, әр іс бәріне бірдей емес. Кел — басқасын табамын.",
    "Very well, child, not every task suits everyone. Come back and I'll find another.")
add("aqsaqal.ezhelgi.abandon_hint", "(Shift+ПКМ — отказаться от задания)", "(Shift+ТЖБ — тапсырмадан бас тарту)",
    "(Shift+RMB to give up the task)")
add("aqsaqal.ezhelgi.batyr", "Ты помог аулу уже %s раз! Народ называет тебя настоящим батыром. Прими дар героев.",
    "Сен ауылға %s рет көмектестің! Халық сені нағыз батыр дейді. Батырлардың сыйын ал.",
    "You have helped the aul %s times! People call you a true batyr. Accept this gift of heroes.")

# ------------------------------------------------------------------ задания
Q = {
    "otau_kiiz": (("У нас женится младший сын — нужно поставить отау, юрту для молодых.",
                   "Кенже ұлымыз үйленгелі жатыр — жастарға отау тігу керек.", "Our youngest son is marrying — we must raise an otau, a yurt for the couple."),
                  ("Принеси %s белой шерсти на войлок.", "Киізге %s ақ жүн әкел.", "Bring %s white wool for the felt."),
                  ("Отау будет тёплым и светлым. Спасибо тебе от молодых!", "Отау жылы әрі жарық болады. Жастардан рахмет!",
                   "The otau will be warm and bright. Thanks from the young couple!")),
    "toi_baursak": (("Завтра той — праздник в честь рождения внука! Гостей будет много.",
                     "Ертең той — немеренің туғанына! Қонақ көп болады.", "Tomorrow is a toi for my grandson's birth! Many guests will come."),
                    ("Принеси %s баурсаков к дастархану.", "Дастарханға %s бауырсақ әкел.", "Bring %s baursaks for the dastarkhan."),
                    ("Ай, какие пышные! Дастархан будет щедрым.", "Әй, қандай үлпілдек! Дастархан мол болады.",
                     "Ah, so fluffy! The table will be generous.")),
    "qymyz": (("Старый Бекет заболел. Знахарь говорит, ему поможет свежий кумыс.",
               "Бекет қарт ауырып қалды. Емші оған саумал қымыз көмектеседі дейді.", "Old Beket fell ill. The healer says fresh kumys will help."),
              ("Принеси %s бутылки кумыса.", "%s бөтелке қымыз әкел.", "Bring %s bottles of kumys."),
              ("Кумыс — лучшее лекарство степи. Бекет скоро встанет.", "Қымыз — даланың ең жақсы емі. Бекет тез тұрады.",
               "Kumys is the steppe's best medicine. Beket will soon be up.")),
    "beshbarmak": (("К нам едут сваты из соседнего аула. Нужно сварить бешбармак.",
                    "Көрші ауылдан құдалар келе жатыр. Бешбармақ асу керек.", "Matchmakers are coming from the next aul. We must cook beshbarmak."),
                   ("Принеси %s сырой баранины.", "%s шикі қой етін әкел.", "Bring %s raw mutton."),
                   ("Казан закипает! Сваты уедут сытыми и довольными.", "Қазан қайнап жатыр! Құдалар тоқ әрі риза болып қайтады.",
                    "The cauldron boils! The matchmakers will leave full and pleased.")),
    "otyn": (("Зима близко, а дров мало. Старикам и детям будет холодно.",
              "Қыс жақын, отын аз. Қарттар мен балалар тоңады.", "Winter is near and there is little firewood. Elders and children will be cold."),
             ("Принеси %s брёвен любого дерева.", "Кез келген ағаштан %s бөрене әкел.", "Bring %s logs of any wood."),
             ("Теперь очаги будут гореть всю зиму. Рахмет!", "Енді ошақтар қыс бойы жанады. Рахмет!",
              "Now the hearths will burn all winter. Thank you!")),
    "shop": (("Прошлой зимой был джут — гололёд, кони не могли достать траву.",
              "Өткен қыста жұт болды — көктайғақтан жылқылар шөп ала алмады.", "Last winter there was a zhut — ice, and the horses couldn't reach grass."),
             ("Принеси %s тюков сена про запас.", "Қорға %s шөп буын әкел.", "Bring %s hay bales for the reserve."),
             ("Теперь табун переживёт любую зиму.", "Енді жылқы кез келген қыстан аман шығады.", "Now the herd will survive any winter.")),
    "teri": (("Шорник Ораз взялся шить сёдла для молодых джигитов.",
              "Ораз ершісі жас жігіттерге ер-тұрман тігуге кірісті.", "Oraz the saddler is making saddles for the young riders."),
             ("Принеси %s кожи.", "%s тері әкел.", "Bring %s leather."),
             ("Хорошая кожа! Сёдла выйдут на славу.", "Жақсы тері! Ерлер керемет болады.", "Fine leather! The saddles will be splendid.")),
    "bal": (("У невестки кашель. Мёд с горячим молоком — старое средство.",
             "Келінім жөтеліп жүр. Ыстық сүтпен бал — көне ем.", "My daughter-in-law has a cough. Honey with hot milk is an old remedy."),
            ("Принеси %s бутылки мёда.", "%s бөтелке бал әкел.", "Bring %s honey bottles."),
            ("Сладкое лекарство! Спасибо, что заботишься о нас.", "Тәтті ем! Бізге қамқор болғаныңа рахмет.",
             "A sweet remedy! Thank you for caring for us.")),
    "kitap": (("Дети аула хотят учиться грамоте, как учил Абай. А книг у нас нет.",
               "Ауыл балалары Абай үйреткендей хат танығысы келеді. Ал кітабымыз жоқ.", "The aul children want to learn to read, as Abai taught. But we have no books."),
              ("Принеси %s книг.", "%s кітап әкел.", "Bring %s books."),
              ("Знание — свет! Дети будут помнить тебя.", "Білім — нұр! Балалар сені ұмытпайды.", "Knowledge is light! The children will remember you.")),
    "qauyrsyn": (("Мерген Қасым делает стрелы для охоты, но перьев не хватает.",
                  "Мерген Қасым аңға жебе жасап жатыр, бірақ қауырсын жетпейді.", "Qasym the marksman makes hunting arrows but lacks feathers."),
                 ("Принеси %s перьев.", "%s қауырсын әкел.", "Bring %s feathers."),
                 ("Теперь стрелы полетят точно в цель.", "Енді жебелер дәл тиеді.", "Now the arrows will fly true.")),
    "an_eti": (("Охотники вернулись пустыми — дичь ушла в горы.",
                "Аңшылар құр қол қайтты — аң тауға кетіпті.", "The hunters came back empty — the game has gone to the mountains."),
               ("Добудь в степи и горах %s куска дичи (джейран, тау-теке).", "Дала мен таудан %s аң етін әкел (қарақұйрық, тау теке).",
                "Hunt %s pieces of game (gazelle, ibex)."),
               ("Сегодня в каждой юрте будет мясо!", "Бүгін әр үйде ет болады!", "Tonight there will be meat in every yurt!")),
    "temir": (("Кузнец взялся ковать клинки для наших батыров, но железа нет.",
               "Ұста батырларымызға қылыш соғуға кірісті, бірақ темір жоқ.", "The smith will forge blades for our batyrs, but there is no iron."),
              ("Принеси %s железных слитков.", "%s темір құйма әкел.", "Bring %s iron ingots."),
              ("Звон молота разнесётся по всей степи!", "Балға дыбысы бүкіл далаға тарайды!", "The hammer's ring will echo across the steppe!")),
    "tenge": (("Старые монеты Отрара помнят Великий шёлковый путь. Хочу собрать их для внуков.",
               "Отырардың көне теңгелері Ұлы Жібек жолын есте сақтаған. Немерелерге жинағым келеді.",
               "Otrar's old coins remember the Great Silk Road. I want to collect them for my grandchildren."),
              ("Найди %s монет Отрара.", "%s Отырар теңгесін тап.", "Find %s Otrar coins."),
              ("Ай, какая чеканка! Это настоящая история.", "Әй, қандай таңба! Бұл нағыз тарих.", "Ah, such minting! This is true history.")),
    "qumyra": (("Хочу показать детям, как жили наши предки в Отраре.",
                "Балаларға ата-бабамыздың Отырарда қалай өмір сүргенін көрсеткім келеді.", "I want to show the children how our ancestors lived in Otrar."),
               ("Принеси %s древних кувшина.", "%s көне құмыра әкел.", "Bring %s ancient jugs."),
               ("Глина помнит руки мастера. Спасибо!", "Саз шебердің қолын сақтаған. Рахмет!", "The clay remembers the potter's hands. Thanks!")),
    "muiiz": (("Мерген хочет сделать роговой лук, как у древних саков.",
               "Мерген көне сақтардікі сияқты мүйіз садақ жасағысы келеді.", "The marksman wants to make a horn bow like the ancient Saka."),
              ("Принеси %s рога архара. Архары живут в горах.", "%s арқар мүйізін әкел. Арқарлар тауда жүреді.",
               "Bring %s argali horns. Argali live in the mountains."),
              ("Из этих рогов выйдет лук, что бьёт дальше всех!", "Бұл мүйіздерден ең алысқа ататын садақ шығады!",
               "These horns will make a bow that shoots farthest of all!")),
    "tymaq": (("Внук пойдёт зимой пасти табун. Ему нужен тёплый тымақ.",
               "Немерем қыста жылқы бағуға шығады. Оған жылы тымақ керек.", "My grandson will herd horses this winter. He needs a warm tymaq."),
              ("Принеси %s лисьих шкуры (корсаки бегают по степи).", "%s түлкі терісін әкел (қарсақтар далада жүгіреді).",
               "Bring %s fox pelts (corsacs run in the steppe)."),
              ("Тымақ выйдет на славу — никакой буран не страшен!", "Тымақ керемет болады — ешбір боран қорқынышты емес!",
               "The tymaq will be splendid — no blizzard will be frightening!")),
    "kieli_qyl": (("Наш кюйши порвал струну. А простая струна не звучит, как надо.",
                   "Күйшіміздің ішегі үзілді. Қарапайым ішек керегіндей дыбыстамайды.", "Our kuishi broke a string. A plain string doesn't sound right."),
                  ("Добудь %s киелі қыл — его хранят тени бақсы у рунных камней.", "%s киелі қыл тап — оны руна тастары маңындағы бақсы көлеңкелері сақтайды.",
                   "Get %s sacred string — the baqsy shadows near rune stones keep it."),
                  ("Теперь домбыра запоёт, как у Құрманғазы!", "Енді домбыра Құрманғазыныкіндей сайрайды!",
                   "Now the dombyra will sing like Kurmangazy's!")),
    "saukele": (("Внучка выходит замуж. По обычаю, невесте шьют сәукеле с золотыми подвесками.",
                 "Немерем ұзатылады. Салт бойынша қалыңдыққа алтын шашақты сәукеле тігеді.", "My granddaughter is marrying. By custom the bride wears a saukele with gold pendants."),
                ("Принеси %s золотых слитков.", "%s алтын құйма әкел.", "Bring %s gold ingots."),
                ("Сәукеле будет сиять, как солнце! Невеста будет самой красивой.", "Сәукеле күндей жарқырайды! Қалыңдық ең сұлу болады.",
                 "The saukele will shine like the sun! The bride will be the most beautiful.")),
    "qasqyr": (("Волки повадились к нашей отаре. Вчера унесли двух ягнят.",
                "Қасқырлар отарымызға үйір болды. Кеше екі қозыны алып кетті.", "Wolves keep coming to our flock. Yesterday they took two lambs."),
               ("Отгони волков: одолей %s.", "Қасқырларды қуып жібер: %s қасқырды жең.", "Drive off the wolves: defeat %s."),
               ("Теперь пастухи будут спать спокойно.", "Енді шопандар тыныш ұйықтайды.", "Now the shepherds will sleep in peace.")),
    "tun": (("По ночам вокруг аула бродит нечисть. Женщины боятся выходить за водой.",
             "Түнде ауыл маңында жын-пері жүреді. Әйелдер суға шығудан қорқады.", "At night evil things roam around the aul. The women fear to fetch water."),
            ("Одолей %s ночных чудищ.", "%s түнгі құбыжықты жең.", "Defeat %s night monsters."),
            ("Ночь снова стала тихой. Ты — защитник аула!", "Түн қайтадан тыныш. Сен — ауылдың қорғаны!", "The night is quiet again. You protect the aul!")),
    "orymshek": (("В пещере за аулом завелись огромные пауки. Дети туда бегают — страшно!",
                  "Ауыл сыртындағы үңгірде алып өрмекшілер пайда болды. Балалар сонда барады — қорқынышты!",
                  "Giant spiders have appeared in the cave beyond the aul. The children run there — it's frightening!"),
                 ("Одолей %s пауков.", "%s өрмекшіні жең.", "Defeat %s spiders."),
                 ("Теперь дети могут играть без страха.", "Енді балалар қорықпай ойнай алады.", "Now the children can play without fear.")),
    "jungar": (("Разведчики донесли: джунгарский отряд рыщет у наших пастбищ.",
                "Барлаушылар хабарлады: жоңғар жасағы жайылымымызды аңдып жүр.", "Scouts report: a Jungar band prowls near our pastures."),
               ("Прогони их: одолей %s джунгар.", "Оларды қуып жібер: %s жоңғарды жең.", "Drive them off: defeat %s Jungars."),
               ("Ты сражался, как Қабанбай батыр! Земля наша спокойна.", "Қабанбай батырдай шайқастың! Жеріміз аман.",
                "You fought like Qabanbai Batyr! Our land is safe.")),
    "qaraqshy": (("Разбойники ограбили караван у руин Отрара. Купцы боятся ехать к нам.",
                  "Қарақшылар Отырар қирандыларының маңында керуенді тонады. Саудагерлер бізге келуден қорқады.",
                  "Bandits robbed a caravan near the ruins of Otrar. Merchants fear to come to us."),
                 ("Одолей %s разбойников.", "%s қарақшыны жең.", "Defeat %s bandits."),
                 ("Дорога снова безопасна. Торговля оживёт!", "Жол қайта қауіпсіз. Сауда жанданады!", "The road is safe again. Trade will revive!")),
    "albasty": (("У соседей родился ребёнок, а по ночам к ним подкрадывается албасты.",
                 "Көршілерде бала туды, ал түнде оларға албасты жасырынып келеді.", "The neighbours had a baby, and at night an albasty creeps toward them."),
                ("Изгони албасты: одолей её.", "Албастыны қу: оны жең.", "Banish the albasty: defeat her."),
                ("Мать и дитя в безопасности. Пусть растёт батыром!", "Ана мен бала аман. Батыр болып өссін!",
                 "Mother and child are safe. May he grow into a batyr!")),
    "zheztyrnaq": (("Джигиты пропадают у озера — говорят, там жезтырнақ притворяется красавицей.",
                    "Жігіттер көл маңында жоғалып жүр — онда жезтырнақ сұлу қыз болып жүр дейді.",
                    "Young men vanish by the lake — they say a zheztyrnaq poses as a beauty there."),
                   ("Одолей %s жезтырнақ.", "%s жезтырнақты жең.", "Defeat %s zheztyrnaq."),
                   ("Медные когти больше не страшны джигитам.", "Мыс тырнақтар енді жігіттерге қауіпті емес.",
                    "The copper claws threaten our young men no more.")),
    "mystan": (("Мыстан кемпір наводит порчу на скот — коровы перестали давать молоко.",
                "Мыстан кемпір малға сұқ түсіріп жүр — сиырлар сүт бермей қалды.", "Mystan kempir curses the cattle — the cows have stopped giving milk."),
               ("Найди ведьму у леса и одолей её. Осторожно — она оборачивается лисой.",
                "Орман шетінен мыстанды тауып жең. Абай бол — ол түлкіге айналады.",
                "Find the witch by the forest and defeat her. Careful — she turns into a fox."),
               ("Порча снята! Коровы снова мычат весело.", "Сұқ кетті! Сиырлар қайта көңілді мөңірейді.",
                "The curse is lifted! The cows are lowing happily again.")),
    "baqsy": (("У рунных камней бродят тени древних бақсы. Их кобыз сводит людей с ума.",
               "Руна тастары маңында ежелгі бақсылардың көлеңкелері кезеді. Олардың қобызы адамдарды есінен адастырады.",
               "Shadows of ancient baqsy roam the rune stones. Their kobyz drives people mad."),
              ("Упокой %s тени бақсы.", "%s бақсы көлеңкесін тыныштандыр.", "Lay %s baqsy shadows to rest."),
              ("Духи успокоились. Пусть их кюй звучит только в памяти.", "Аруақтар тынышталды. Олардың күйі тек жадымызда қалсын.",
               "The spirits are calm. Let their kui sound only in memory.")),
    "jalmauyz": (("Жалмауыз кемпір — семиглавая людоедка — утащила пастушка в своё логово!",
                  "Жалмауыз кемпір — жеті басты — шопан баланы апанына алып кетті!", "Zhalmauyz kempir, the man-eater, dragged a shepherd boy to her lair!"),
                 ("Одолей жалмауыз кемпір. Это под силу только настоящему батыру.", "Жалмауыз кемпірді жең. Бұған тек нағыз батыр шамасы жетеді.",
                  "Defeat zhalmauyz kempir. Only a true batyr can do it."),
                 ("Ты победил древнее зло! О тебе будут петь жыршы.", "Ежелгі зұлымдықты жеңдің! Сен туралы жыршылар жыр айтады.",
                  "You defeated an ancient evil! Bards will sing of you.")),
    "dau": (("У пещеры в горах поселился дәу — великан. Он ест быков целиком!",
             "Таудағы үңгірге дәу қоныстанды. Ол өгізді тұтас жейді!", "A dau — a giant — has settled in a mountain cave. He eats bulls whole!"),
            ("Одолей дәу.", "Дәуді жең.", "Defeat the dau."),
            ("Великан повержен! Как в сказках Ер Төстік!", "Дәу жеңілді! Ер Төстік ертегілеріндегідей!", "The giant has fallen! Just like in the tales of Er Tostik!")),
    "aidahar": (("Айдахар проснулся в горах Алатау. Его огонь сжёг летние пастбища, народ в страхе.",
                 "Алатауда айдаһар оянды. Оның оты жайлауларды өртеп жіберді, халық үрейде.",
                 "The aidahar has woken in the Alatau. Its fire burned the summer pastures; the people are afraid."),
                ("Найди логово дракона на горных вершинах и одолей его.", "Тау шыңдарынан айдаһардың апанын тауып, оны жең.",
                 "Find the dragon's lair on the mountain peaks and slay it."),
                ("Ты сразил айдахара! Отныне имя твоё — в одном ряду с Қобыланды и Алпамысом!",
                 "Айдаһарды өлтірдің! Енді атың Қобыланды мен Алпамыспен қатар тұрады!",
                 "You slew the aidahar! Your name now stands beside Qobylandy and Alpamys!")),
    "jogalgan_at": (("Беда, балам! Мой любимый конь Ақбоз ушёл ночью из табуна и не вернулся.",
                     "Бәле, балам! Сүйікті атым Ақбоз түнде үйірден кетіп, қайтпады.",
                     "Trouble, child! My beloved horse Aqboz left the herd last night and hasn't returned."),
                    ("Пастухи видели его на %s. Найди его и приведи ко мне (на поводке или верхом).",
                     "Шопандар оны %s көріпті. Тауып, маған әкел (жетекпен не мініп).",
                     "Shepherds saw him to the %s. Find him and bring him to me (on a lead or riding)."),
                    ("Ақбоз вернулся! Возьми его себе — он будет служить тебе верно.",
                     "Ақбоз қайтып келді! Оны өзіңе ал — саған адал қызмет етеді.",
                     "Aqboz is back! Keep him — he will serve you faithfully.")),
    "tanba": (("В молодости я видел в ущелье Тамгалы рисунки предков. Хочу знать, целы ли они.",
               "Жас кезімде Тамғалы шатқалында ата-баба суреттерін көргенмін. Аман ба екен, білгім келеді.",
               "In my youth I saw the ancestors' drawings in the Tamgaly gorge. I want to know if they survive."),
              ("Найди %s новых петроглифа (таңба тас) в скалах Тамгалы.", "Тамғалы жартастарынан %s жаңа таңба тас тап.",
               "Find %s new petroglyphs (tanba tas) in the Tamgaly rocks."),
              ("Значит, рисунки живы! Память предков не стёрлась.", "Демек, суреттер аман! Ата-баба жады өшпеген.",
               "So the drawings live! The memory of the ancestors is not erased.")),
    "togyz": (("Давно со мной никто не играл в тоғыз құмалақ. Старики все заняты...",
               "Көптен бері ешкім менімен тоғыз құмалақ ойнамады. Қарттардың бәрі бос емес...",
               "Nobody has played togyz qumalaq with me for a long time. The old men are all busy..."),
              ("Обыграй меня в тоғыз құмалақ: найди доску в ауле и сыграй против аксакала.",
               "Мені тоғыз құмалақтан ұт: ауылдан тақта тауып, ақсақалға қарсы ойна.",
               "Beat me at togyz qumalaq: find a board in the aul and play against the elder."),
              ("Ай, хитро сыграл! Давно меня так не обыгрывали.", "Әй, айлалы ойнадың! Мені көптен бері бұлай ешкім ұтпаған.",
               "Ah, cleverly played! Nobody has beaten me like that in a long time.")),
}
for q, (story, task, thanks) in Q.items():
    add(f"quest.ezhelgi.{q}.story", *story)
    add(f"quest.ezhelgi.{q}.task", *task)
    add(f"quest.ezhelgi.{q}.thanks", *thanks)
add("quest.ezhelgi.line.bring", "Задание: принести %s × %s", "Тапсырма: %s × %s әкел", "Task: bring %s × %s")
add("quest.ezhelgi.line.kill", "Задание: одолеть «%s» — %s / %s", "Тапсырма: «%s» жең — %s / %s", "Task: defeat \"%s\" — %s / %s")
add("quest.ezhelgi.line.horse", "Задание: найти коня Ақбоз (он светится) и привести к аксакалу",
    "Тапсырма: Ақбоз атты тауып (ол жарқырайды) ақсақалға әкел", "Task: find the horse Aqboz (it glows) and bring it to the elder")
add("quest.ezhelgi.line.glyphs", "Задание: найти петроглифы — %s / %s", "Тапсырма: таңба табу — %s / %s", "Task: find petroglyphs — %s / %s")
add("quest.ezhelgi.line.togyz", "Задание: выиграть у аксакала в тоғыз құмалақ", "Тапсырма: ақсақалды тоғыз құмалақтан ұту",
    "Task: beat the elder at togyz qumalaq")
add("quest.ezhelgi.any_logs", "брёвна", "бөрене", "logs")
add("quest.ezhelgi.any_monster", "ночные чудища", "түнгі құбыжықтар", "night monsters")
add("quest.ezhelgi.progress", "%s: %s / %s", "%s: %s / %s", "%s: %s / %s")
add("quest.ezhelgi.ready", "%s: %s / %s — вернись к аксакалу!", "%s: %s / %s — ақсақалға қайт!", "%s: %s / %s — return to the elder!")
add("quest.ezhelgi.togyz_won", "Ты обыграл аксакала! Вернись к нему за наградой.", "Ақсақалды ұттың! Сыйлық үшін оған қайт.",
    "You beat the elder! Return to him for your reward.")
add("quest.ezhelgi.horse_name", "Ақбоз", "Ақбоз", "Aqboz")
for d, v in {"north": ("севере", "солтүстікте", "north"), "northeast": ("северо-востоке", "солтүстік-шығыста", "northeast"),
             "east": ("востоке", "шығыста", "east"), "southeast": ("юго-востоке", "оңтүстік-шығыста", "southeast"),
             "south": ("юге", "оңтүстікте", "south"), "southwest": ("юго-западе", "оңтүстік-батыста", "southwest"),
             "west": ("западе", "батыста", "west"), "northwest": ("северо-западе", "солтүстік-батыста", "northwest"),
             "far": ("краю степи", "дала шетінде", "edge of the steppe")}.items():
    add(f"direction.ezhelgi.{d}", *v)


def names():
    return dict(N)
