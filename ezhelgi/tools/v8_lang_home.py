"""v8: тексты кухни, мебели, природы, инструментов, набега, волков и ярмарки (ru_ru, kk_kz, en_us)."""

N = {}


def add(key, ru, kk, en):
    N[key] = (ru, kk, en)


def many(prefix, items):
    for i, v in enumerate(items):
        add(f"{prefix}.{i}", *v)


for k, v in {
    "kazan": ("Қазан (котёл)", "Қазан", "Qazan Cauldron"),
    "tandyr": ("Тандыр", "Тандыр", "Tandyr Oven"),
    "keptirgish": ("Кептіргіш (сушилка)", "Кептіргіш", "Drying Rack"),
    "samauyr": ("Самауыр (самовар)", "Самауыр", "Samauyr (Samovar)"),
    "dastarkhan": ("Дастархан", "Дастарқан", "Dastarkhan Low Table"),
    "korpe": ("Көрпе (подстилка)", "Көрпе", "Korpe Floor Cushion"),
    "kebezhe": ("Кебеже (сундук)", "Кебеже", "Kebezhe Chest"),
    "besik": ("Бесік (колыбель)", "Бесік", "Besik Cradle"),
    "qyzgaldaq_shrenk": ("Тюльпан Шренка", "Шренк қызғалдағы", "Schrenk's Tulip"),
    "qyzgaldaq_greig": ("Тюльпан Грейга", "Грейг қызғалдағы", "Greig's Tulip"),
    "seleu": ("Ковыль (селеу)", "Селеу", "Feather Grass"),
    "sivers_japyragy": ("Листва яблони Сиверса", "Сиверс алмасының жапырағы", "Sievers Apple Leaves"),
    "alma_kosheti": ("Саженец яблони Сиверса", "Алма көшеті", "Sievers Apple Sapling"),
    "sekseuil": ("Саксаул", "Сексеуіл", "Saxaul Log"),
    "sekseuil_japyragy": ("Ветки саксаула", "Сексеуіл бұтағы", "Saxaul Branches"),
    "sekseuil_kosheti": ("Саженец саксаула", "Сексеуіл көшеті", "Saxaul Sapling"),
}.items():
    add(f"block.ezhelgi.{k}", *v)

for k, v in {
    "nan": ("Нан (лепёшка)", "Нан", "Tandyr Nan"),
    "samsa": ("Самса", "Самса", "Samsa"),
    "qaq_et": ("Қақ ет (вяленое мясо)", "Қақ ет", "Dried Meat"),
    "sorpa": ("Сорпа (бульон)", "Сорпа", "Sorpa Broth"),
    "quyrdaq": ("Қуырдақ (жаркое)", "Қуырдақ", "Quyrdaq Roast"),
    "palau": ("Палау (плов)", "Палау", "Palau (Pilaf)"),
    "nauryz_kozhe": ("Наурыз-көже", "Наурыз көже", "Nauryz Kozhe"),
    "qara_shai": ("Қара шай (чёрный чай)", "Қара шай", "Black Tea"),
    "sutti_shai": ("Сүтті шай (чай с молоком)", "Сүтті шай", "Tea with Milk"),
    "sybyzgy": ("Сыбызғы (флейта)", "Сыбызғы", "Sybyzgy Flute"),
    "zhetigen": ("Жетіген (семиструнная арфа)", "Жетіген", "Zhetigen Harp"),
    "shanqobyz": ("Шаңқобыз (варган)", "Шаңқобыз", "Shanqobyz Jaw Harp"),
    "dauylpaz": ("Дауылпаз (боевой барабан)", "Дауылпаз", "Dauylpaz War Drum"),
    "qanbaq_spawn_egg": ("Яйцо призыва перекати-поля", "Қаңбақ жұмыртқасы", "Tumbleweed Spawn Egg"),
}.items():
    add(f"item.ezhelgi.{k}", *v)
add("entity.ezhelgi.qanbaq", "Перекати-поле (қаңбақ)", "Қаңбақ", "Tumbleweed")
add("entity.ezhelgi.seat", "Сиденье", "Орындық", "Seat")
add("container.ezhelgi.kebezhe", "Кебеже", "Кебеже", "Kebezhe")

add("tooltip.ezhelgi.instrument.sybyzgy", "Созывает домашних животных к тебе", "Үй жануарларын шақырады", "Calls nearby animals to you")
add("tooltip.ezhelgi.instrument.zhetigen", "Мелодия лечит всех рядом", "Әуен маңайдағыларды емдейді", "The melody heals everyone nearby")
add("tooltip.ezhelgi.instrument.shanqobyz", "Ночное зрение; нечисть рядом подсвечивается",
    "Түнгі көру; маңайдағы жын-пері жарқырайды", "Night vision; nearby monsters glow")
add("tooltip.ezhelgi.instrument.dauylpaz", "Боевой бой: сила и скорость тебе и союзникам",
    "Соғыс ырғағы: саған және одақтастарға күш пен жылдамдық", "War beat: strength and speed for you and allies")

for k, v in {
    "kazan.need_water": ("Налей в казан ведро воды", "Қазанға бір шелек су құй", "Pour a bucket of water into the qazan"),
    "kazan.hint": ("Положи продукты: мясо — сорпа; +картофель — қуырдақ; +морковь и пшеница — палау",
                   "Өнім сал: ет — сорпа; +картоп — қуырдақ; +сәбіз бен бидай — палау",
                   "Add food: meat = sorpa; +potato = quyrdaq; +carrot and wheat = palau"),
    "kazan.not_food": ("Это в казан не кладут", "Мұны қазанға салмайды", "That doesn't go into a qazan"),
    "kazan.already": ("%s уже в казане", "%s қазанда бар", "%s is already in the qazan"),
    "kazan.added": ("В казан: %s", "Қазанға: %s", "Into the qazan: %s"),
    "kazan.full": ("Казан полон", "Қазан толы", "The qazan is full"),
    "kazan.unknown": ("Такого блюда нет. Наурыз-көже: мясо, пшеница, молоко, құрт, морковь, свёкла. Shift+ПКМ — вылить",
                      "Мұндай тағам жоқ. Наурыз көже: ет, бидай, сүт, құрт, сәбіз, қызылша. Shift+ПКМ — төгу",
                      "No such dish. Nauryz kozhe: meat, wheat, milk, qurt, carrot, beetroot. Shift+use to empty"),
    "kazan.no_fire": ("Будет %s — нужен огонь под казаном (костёр)", "%s болады — қазан астына от керек",
                      "It will be %s — light a fire under the qazan"),
    "kazan.cooking": ("Варится %s: %s%%", "%s пісіп жатыр: %s%%", "Cooking %s: %s%%"),
    "kazan.ready": ("Готово: %s (мисок: %s). Возьми миску", "Дайын: %s (%s тостаған). Тостаған ал", "Ready: %s (%s bowls). Use a bowl"),
    "kazan.emptied": ("Казан опустошён", "Қазан босатылды", "The qazan is emptied"),
    "tandyr.full": ("На стенках тандыра нет места", "Тандыр қабырғасында орын жоқ", "No room left in the tandyr"),
    "tandyr.need_wheat": ("Нужна пшеница (2 для нана или 1 для самсы)", "Бидай керек (нанға 2, самсаға 1)",
                          "Need wheat (2 for nan, 1 for samsa)"),
    "tandyr.need_fuel": ("Разожги тандыр углём, дровами или саксаулом", "Тандырды көмір, отын немесе сексеуілмен жақ",
                         "Light the tandyr with coal, wood or saxaul"),
    "tandyr.status": ("Тандыр: печётся %s из %s, огня ещё на %s с", "Тандыр: %s/%s пісіп жатыр, от тағы %s с",
                      "Tandyr: %s of %s baking, fire for %s s"),
    "keptirgish.full": ("Кептіргіш занят", "Кептіргіш толы", "The rack is full"),
    "keptirgish.hint": ("Повесь сырое мясо или поставь ведро молока — высохнет в қақ ет и құрт",
                        "Шикі ет ілі немесе шелек сүт қой — қақ ет пен құрт болады",
                        "Hang raw meat or place a milk bucket to make dried meat and qurt"),
    "keptirgish.status": ("Сохнет: %s, готовность %s%%. На солнце быстрее", "Кебуде: %s, %s%%. Күнде тезірек",
                          "Drying: %s, %s%%. Faster in the sun"),
    "keptirgish.took": ("Снято: %s", "Алынды: %s", "Taken: %s"),
    "samauyr.need_water": ("Налей в самауыр воды", "Самауырға су құй", "Fill the samauyr with water"),
    "samauyr.need_fuel": ("Положи угли или саксаул в трубу", "Мұржаға көмір не сексеуіл сал", "Put coal or saxaul into the chimney"),
    "samauyr.heating": ("Самауыр закипает: %s%%", "Самауыр қайнап келеді: %s%%", "The samauyr is heating: %s%%"),
    "samauyr.ready": ("Кипит! Чая: %s пиал, молока: %s. Наливай в пузырёк", "Қайнады! Шай: %s кесе, сүт: %s",
                      "Boiling! Tea: %s cups, milk: %s. Use a glass bottle"),
    "besik.lullaby": ("Әлди-әлди… колыбельная успокаивает всех вокруг", "Әлди-әлди, ақ бөпем…", "Aldi-aldi… the lullaby soothes everyone"),
    "sybyzgy": ("Сыбызғы звучит над степью — животных идёт: %s", "Сыбызғы үні — жануарлар келе жатыр: %s",
                "The sybyzgy calls — animals coming: %s"),
    "spring": ("Көктем! Пришла весна — степь покрывается тюльпанами Шренка и Грейга",
               "Көктем келді — дала Шренк пен Грейг қызғалдақтарына толды",
               "Spring has come — the steppe is covered in wild tulips"),
    "zharmenke": ("Жәрмеңке! В караван-сарай приехали купцы с редкими товарами",
                  "Жәрмеңке! Керуен-сарайға сирек тауар әкелген саудагерлер келді",
                  "Fair day! Merchants with rare goods have come to the caravanserai"),
}.items():
    add(f"message.ezhelgi.{k}", *v)

many("aul.ezhelgi.raid", [
    ("Жау келді! Джунгары идут на аул!", "Жау келді! Жоңғарлар ауылға келе жатыр!", "Enemies! The Jungars are coming!"),
    ("Батыры, на коней! Защитим аул!", "Батырлар, атқа қон! Ауылды қорғайық!", "Batyrs, to horse! Defend the aul!"),
    ("Кто встанет за аул — тому почёт и награда.", "Ауыл үшін тұрғанға — құрмет пен сый.", "Whoever stands for the aul earns honour and reward."),
])
for k, v in {
    "raid.wave1": ("Первый отряд джунгар у аула!", "Жоңғарлардың бірінші тобы ауылда!", "The first Jungar band is at the aul!"),
    "raid.wave2": ("Идёт главный отряд джунгар!", "Жоңғарлардың негізгі тобы келе жатыр!", "The main Jungar host is coming!"),
    "raid.won": ("Набег отбит! Аул спасён.", "Шабуыл тойтарылды! Ауыл аман.", "The raid is repelled! The aul is saved."),
    "raid.lost": ("Джунгары ушли с добычей…", "Жоңғарлар олжамен кетті…", "The Jungars left with their plunder…"),
    "raid.thanks": ("Рахмет, батыр! Аул не забудет твоей храбрости. Возьми доспех врага.",
                    "Рахмет, батыр! Ауыл ерлігіңді ұмытпайды. Жаудың сауытын ал.",
                    "Thank you, batyr! The aul will not forget your courage. Take the enemy's armour."),
    "wolves.won": ("Волчья стая перебита. Загоны целы.", "Қасқырлар қырылды. Қора аман.", "The wolf pack is slain. The pens are safe."),
    "wolves.lost": ("Стая ушла в степь.", "Қасқырлар далаға кетті.", "The pack went back into the steppe."),
    "wolves.thanks": ("Спасибо, что уберёг скот! Вот тебе қазы.", "Малды сақтағаның үшін рахмет! Мә, қазы.",
                      "Thank you for saving the herd! Here is some qazy."),
    "respect": ("Уважение аула: %s", "Ауылдың құрметі: %s", "Respect of the aul: %s"),
}.items():
    add(f"aul.ezhelgi.{k}", *v)
many("aul.ezhelgi.wolves", [
    ("Қасқыр! Голодная стая подкралась к загонам!", "Қасқыр! Аш қасқырлар қораға жақындады!", "Wolves! A hungry pack is creeping up on the pens!"),
    ("Берите факелы — отгоните волков от скота!", "Шырақ алыңдар — қасқырды малдан қуыңдар!", "Grab torches — drive the wolves from the herd!"),
])

for k, v in {
    "sybyzgy": ("Играет сыбызғы", "Сыбызғы тартылады", "Sybyzgy plays"),
    "zhetigen": ("Звенит жетіген", "Жетіген шертіледі", "Zhetigen rings"),
    "shanqobyz": ("Гудит шаңқобыз", "Шаңқобыз ызыңдайды", "Shanqobyz twangs"),
    "dauylpaz": ("Бьёт дауылпаз", "Дауылпаз соғылады", "Dauylpaz beats"),
    "kazan": ("Булькает казан", "Қазан бұрқылдайды", "Qazan bubbles"),
    "samauyr": ("Кипит самауыр", "Самауыр қайнайды", "Samauyr boils"),
}.items():
    add(f"subtitles.ezhelgi.{k}", *v)


def names():
    return dict(N)
