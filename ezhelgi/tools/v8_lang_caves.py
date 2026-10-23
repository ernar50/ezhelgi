"""v8: тексты для пещер, мест и пещерных существ (ru_ru, kk_kz, en_us)."""

N = {}


def add(key, ru, kk, en):
    N[key] = (ru, kk, en)


def many(prefix, items):
    for i, v in enumerate(items):
        add(f"{prefix}.{i}", *v)


for k, v in {
    "zhylan": ("Пещерный жылан (змея)", "Үңгір жыланы", "Cave Snake"),
    "zhyn": ("Жын", "Жын", "Zhyn (Cave Demon)"),
    "shahmaran": ("Шахмаран — царь змей", "Шахмаран — Жылан патша", "Shahmaran, King of Snakes"),
    "shoiynqulaq": ("Шойынқұлақ", "Шойынқұлақ", "Shoiynqulaq the Iron-Eared"),
    "darwish": ("Дервиш-отшельник", "Дәруіш", "Hermit Darwish"),
}.items():
    add(f"entity.ezhelgi.{k}", *v)
    add(f"item.ezhelgi.{k}_spawn_egg", f"Яйцо призыва: {v[0]}", f"{v[1]} жұмыртқасы", f"{v[2]} Spawn Egg")

for k, v in {
    "qola": ("Қола (бронза)", "Қола", "Qola (Bronze)"),
    "qola_qanjar": ("Бронзовый кинжал", "Қола қанжар", "Bronze Dagger"),
    "qola_balta": ("Бронзовый топор", "Қола балта", "Bronze Axe"),
    "zhylan_tumary": ("Змеиный тұмар", "Жылан тұмары", "Serpent Amulet"),
    "shoiyn_qulaq": ("Чугунное ухо великана", "Шойын құлақ", "Iron Ear of the Giant"),
}.items():
    add(f"item.ezhelgi.{k}", *v)
add("tooltip.ezhelgi.zhylan_tumary", "+4 к здоровью, видишь во тьме", "+4 денсаулық, қараңғыда көресің", "+4 max health, night vision")
add("tooltip.ezhelgi.shoiyn_qulaq", "+3 к прочности брони", "+3 сауыт беріктігі", "+3 armor toughness")

add("message.ezhelgi.shahmaran.angry", "Шахмаран гневается и не станет говорить", "Шахмаран ашулы, сөйлеспейді",
    "Shahmaran is angry and will not speak")
add("shahmaran.ezhelgi.greet", "Ты пришёл без меча в руке — значит, пришёл за словом. Садись, путник.",
    "Қолыңда қылыш жоқ — демек сөз іздеп келдің. Отыр, жолаушы.",
    "You came without a blade raised, so you came for words. Sit, traveller.")
add("shahmaran.ezhelgi.gift", "Возьми этот тұмар. Пока он с тобой, змеи помнят, что ты мой гость.",
    "Мына тұмарды ал. Ол сенде тұрғанда жыландар сенің менің қонағым екеніңді біледі.",
    "Take this amulet. While you wear it, the serpents remember you are my guest.")
add("shahmaran.ezhelgi.knowledge", "Шахмаран открывает тебе глаза: тьма больше не скрывает пути, яд покидает кровь.",
    "Шахмаран көзіңді ашады: қараңғылық жолды жасырмайды, у қаныңнан шығады.",
    "Shahmaran opens your eyes: darkness no longer hides the way, and the poison leaves your blood.")
many("shahmaran.ezhelgi.wisdom", [
    ("Кто роет колодец другому, сам пьёт из него последним.", "Біреуге құдық қазған соңынан өзі ішеді.",
     "Whoever digs a well for another drinks from it last."),
    ("Змея меняет кожу, но не меняет сердца. Смотри на дела, а не на узоры.", "Жылан терісін ауыстырады, жүрегін емес. Өрнекке емес, іске қара.",
     "A snake sheds its skin, not its heart. Judge deeds, not patterns."),
    ("Медь лежит глубоко на западе степи. Древние плавили её, когда твой народ ещё не знал железа.",
     "Мыс даланың батысында терең жатыр. Ертедегілер оны темірді білмей тұрып балқытқан.",
     "Copper lies deep in the western steppe. The ancients smelted it before your people knew iron."),
    ("Под степью есть колодец без дна. Внизу — царство великана с чугунными ушами. Иди туда только сильным.",
     "Дала астында түбі жоқ құдық бар. Төменде — шойын құлақты алыптың патшалығы. Тек күшті болсаң бар.",
     "Beneath the steppe is a bottomless well. Below lies the realm of the iron-eared giant. Go only when strong."),
    ("Спящего дәу обходи на цыпочках. Великаны спят чутко, а просыпаются голодными.",
     "Ұйықтап жатқан дәуді ақырын айналып өт. Алыптар сергек ұйықтайды, аш оянады.",
     "Tiptoe past a sleeping dau. Giants sleep lightly and wake up hungry."),
    ("Жын боится света и молитвы. Чёрный крадётся за спиной, красный жжёт, синий морозит.",
     "Жын жарықтан қорқады. Қарасы арқаңнан келеді, қызылы күйдіреді, көгі мұздатады.",
     "A zhyn fears light. The black one sneaks behind you, the red one burns, the blue one freezes."),
    ("Мудрость — как вода в пещере: капля за каплей, и камень становится сосулькой.",
     "Даналық — үңгірдегі су сияқты: тамшы-тамшыдан тас та сүңгі болады.",
     "Wisdom is like water in a cave: drop by drop, even stone grows into an icicle."),
    ("В горах Каратау есть пещера, где солнце падает сквозь свод и растут деревья. Там живу я.",
     "Қаратау тауында күн күмбезден түсетін, ағаш өсетін үңгір бар. Мен сонда тұрамын.",
     "In the Karatau hills is a cave where sunlight falls through the roof and trees grow. That is my home."),
    ("Не всякого врага нужно убивать. Иногда достаточно с ним поговорить.",
     "Әр жауды өлтіру шарт емес. Кейде сөйлесу жеткілікті.",
     "Not every foe must be slain. Sometimes it is enough to talk."),
    ("Старик в горной келье знает больше книг, чем звёзд на небе. Принеси ему изумруды.",
     "Тау үңгіріндегі қария аспандағы жұлдыздан көп кітап біледі. Оған зүмірет апар.",
     "The old man in the mountain cell knows more books than there are stars. Bring him emeralds."),
])
many("darwish.ezhelgi.advice", [
    ("Тише едешь — дальше будешь, но в степи без коня не уедешь вовсе.", "Асықпаған арбамен қоянды қуып жетер, бірақ далада атсыз алысқа бармайсың.",
     "Slow and steady goes far, but in the steppe nobody goes far without a horse."),
    ("Береги воду в бурдюке: в пустыне она дороже золота.", "Торсықтағы суды сақта: шөлде ол алтыннан қымбат.",
     "Keep water in your skin flask: in the desert it is worth more than gold."),
    ("Не копай прямо вниз, сынок. Земля любит открывать пустоты под ногами глупцов.",
     "Тура төмен қазба, балам. Жер ақымақтың аяғының астынан қуыс ашқанды жақсы көреді.",
     "Never dig straight down, my child. The earth loves opening voids beneath fools."),
    ("Гость — от Бога. Накорми путника, и дорога накормит тебя.", "Қонақ — Құдайдан. Жолаушыны тамақтандыр, жол сені тамақтандырады.",
     "A guest is sent by heaven. Feed the traveller, and the road will feed you."),
    ("Зачарованная книга бесполезна, если её читать без разума.", "Сиқырлы кітап ақылсыз оқылса пайдасыз.",
     "An enchanted book is useless when read without understanding."),
    ("Слушай аксакала: его один день стоит твоего года.", "Ақсақалды тыңда: оның бір күні сенің бір жылыңа тұрады.",
     "Listen to the elders: one of their days is worth one of your years."),
    ("Ночью в степи держи огонь рядом. Волк смелеет в темноте.", "Түнде далада отты жаныңда ұста. Қасқыр қараңғыда батылданады.",
     "At night in the steppe keep a fire close. The wolf grows bold in the dark."),
    ("Знание — единственный клад, который не тянет карманы.", "Білім — қалтаны ауырлатпайтын жалғыз қазына.",
     "Knowledge is the only treasure that does not weigh down your pockets."),
    ("Кто спешит за золотом в глубины, часто находит там только дәу.", "Тереңге алтын іздеп асыққан көбіне тек дәу табады.",
     "Whoever rushes into the deep for gold usually finds only a dau."),
    ("Одна свеча прогоняет тьму всей кельи. Будь такой свечой.", "Бір шам бүкіл үңгірдің қараңғысын қуады. Сондай шам бол.",
     "One candle drives the darkness from a whole cell. Be that candle."),
    ("Не хвались, пока не вернулся из похода.", "Жорықтан оралмай мақтанба.",
     "Do not boast before you return from the campaign."),
    ("Терпение — ключ к любому сундуку, даже к запертому сердцу.", "Сабыр — кез келген сандықтың, тіпті жабық жүректің де кілті.",
     "Patience is the key to every chest, even a locked heart."),
])
many("legend.ezhelgi.oqzhetpes", [
    ("Давным-давно враги увели в плен красавицу. Её хотели отдать в жёны силой, но она поставила условие: женихом станет тот, чья стрела долетит до вершины этой скалы.",
     "Ерте заманда жаулар бір сұлуды тұтқынға алды. Оны күшпен күйеуге бермек болды, бірақ ол шарт қойды: оғы осы жартастың басына жеткен адам күйеу болады.",
     "Long ago, raiders took a beautiful girl captive. They meant to marry her off by force, but she set a condition: her husband would be the one whose arrow reached the top of this rock."),
    ("Лучшие стрелки натягивали луки до треска, но ни одна стрела не долетела. Так скалу и прозвали — Оқжетпес, «куда не долетит стрела».",
     "Ең мықты мергендер садағын сықырлатып тартты, бірақ бірде-бір оқ жетпеді. Сондықтан жартасты Оқжетпес деп атады.",
     "The finest archers drew their bows until they creaked, yet no arrow reached it. So the rock was named Oqzhetpes, 'where no arrow reaches'."),
    ("Тогда девушка сама поднялась по узкой тропе на вершину и бросилась в озеро, чтобы не достаться чужим.",
     "Сонда қыз тар соқпақпен шыңға өзі көтеріліп, жатқа бұйырмас үшін көлге секірді.",
     "Then the girl climbed the narrow path to the summit herself and leapt into the lake rather than belong to strangers."),
    ("Там, где она упала, из воды поднялся камень — Жұмбақтас, «камень-загадка». Говорят, на вершине до сих пор лежит то, что не досталось ни одному жениху.",
     "Ол құлаған жерде судан тас көтерілді — Жұмбақтас. Шыңда әлі күнге дейін ешбір күйеуге бұйырмаған дүние жатыр дейді.",
     "Where she fell, a stone rose from the water: Zhumbaqtas, the riddle stone. They say what no suitor ever won still waits at the summit."),
])
add("book.ezhelgi.oqzhetpes", "Легенда об Окжетпесе", "Оқжетпес аңызы", "The Legend of Oqzhetpes")

for k, v in {
    "zhylan": ("Шипит змея", "Жылан ысылдайды", "Snake hisses"),
    "zhyn": ("Шепчет жын", "Жын сыбырлайды", "Zhyn whispers"),
    "shahmaran": ("Говорит Шахмаран", "Шахмаран сөйлейді", "Shahmaran speaks"),
    "shoiynqulaq": ("Ревёт Шойынқұлақ", "Шойынқұлақ ақырады", "Shoiynqulaq roars"),
    "shoiyn_clap": ("Звон чугунных ушей", "Шойын құлақ сыңғырлайды", "Iron ears clang"),
}.items():
    add(f"subtitles.ezhelgi.{k}", *v)


def names():
    return dict(N)
