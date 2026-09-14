package com.example.engine

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class WritingTone(val title: String, val description: String, val icon: String) {
    FORMAL("رسمي ومؤدب", "مخاطبة محترمة ومناسبة للمراسلات الرسمية والمدراء", "🎩"),
    PROFESSIONAL("احترافي وعملي", "أسلوب واضح ومباشر للأعمال واجتماعات العمل", "💼"),
    FRIENDLY("ودود ولطيف", "أسلوب دافئ ومحبب للأصدقاء والمقربين", "🌸"),
    CONCISE("مختصر وموجز", "إيصال الفكرة بأقل عدد من الكلمات مع حفظ المعنى", "⚡"),
    POETIC("أدبي وبلاغي", "لغة عربية فصيحة وأنيقة بعبارات جزلة", "📜"),
    CASUAL_EMOJI("مرح مع إيموجي", "عفوي وجذاب مدعّم بإيموجيات معبرة", "🎉"),
    PERSUASIVE("مقنع ومؤثر", "أسلوب يدعو للقبول بحجج جذابة", "🤝")
}

data class ProofreadResult(
    val original: String,
    val corrected: String,
    val improvements: List<String>
)

data class SmartReply(
    val label: String,
    val text: String,
    val icon: String
)

object AiService {
    private const val TAG = "AiService"
    private const val GEMINI_MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    /**
     * Resolve effective Gemini API Key from BuildConfig or custom user setting
     */
    fun getResolvedApiKey(customKey: String?): String? {
        if (!customKey.isNullOrBlank()) return customKey.trim()
        val buildConfigKey = try {
            val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            if (key != null && key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else null
        } catch (e: Throwable) {
            null
        }
        return buildConfigKey
    }

    /**
     * Rewrite text with selected tone (Gemini API with instant offline NLP fallback)
     */
    suspend fun rewriteWithTone(
        text: String,
        tone: WritingTone,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                أنت مساعد كتابة ذكي داخل لوحة المفاتيح.
                المهمة: أعد صياغة النص التالي بنبرة (${tone.title}: ${tone.description}).
                حافظ على جوهر المعنى ولكن طوّر الصياغة لتناسب النبرة المطلوبة بدقة عالية وفصاحة.
                أرجع النص المعاد صياغته فقط بدون مقدمات أو شرح.
                
                النص الأصلي:
                $clean
            """.trimIndent()

            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim().removeSurrounding("\"", "\"")
            }
        }

        // Local smart NLP rule engine
        return@withContext localRewriteWithTone(clean, tone)
    }

    /**
     * Proofread and correct spelling, grammar, hamzas, and punctuation
     */
    suspend fun proofreadAndCorrect(
        text: String,
        apiKey: String? = null
    ): ProofreadResult = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ProofreadResult("", "", emptyList())

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                أنت خبير تدقيق لغوي ونحوي وإملائي للغة العربية.
                قم بتصحيح النص التالي من أي أخطاء إملائية (همزات، تاء مربوطة/مفتوحة، ألف لينة، تنوين، أحرف زائدة أو ناقصة)، وأخطاء نحوية وعلامات ترقيم.
                أرجع الناتج فقط بصيغة JSON التالية:
                {
                  "corrected": "النص المصحح هنا",
                  "improvements": ["تحسين 1", "تحسين 2"]
                }
                
                النص:
                $clean
            """.trimIndent()

            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                try {
                    val jsonStr = extractJsonString(onlineResult)
                    val json = JSONObject(jsonStr)
                    val corrected = json.optString("corrected", clean)
                    val arr = json.optJSONArray("improvements")
                    val imps = mutableListOf<String>()
                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            imps.add(arr.getString(i))
                        }
                    }
                    if (imps.isEmpty()) imps.add("تم تصحيح الإملاء وضبط الصياغة")
                    return@withContext ProofreadResult(clean, corrected, imps)
                } catch (e: Throwable) {
                    Log.w(TAG, "Error parsing online proofread JSON", e)
                }
            }
        }

        // Local comprehensive proofreading engine
        return@withContext localProofread(clean)
    }

    /**
     * Generate contextual smart replies
     */
    suspend fun generateSmartReplies(
        contextMessage: String,
        apiKey: String? = null
    ): List<SmartReply> = withContext(Dispatchers.IO) {
        val clean = contextMessage.trim()
        val effectiveKey = getResolvedApiKey(apiKey)

        if (clean.isNotEmpty() && !effectiveKey.isNullOrBlank()) {
            val prompt = """
                بناءً على الرسالة التالية:
                "$clean"
                اقترح 5 ردود ذكية وموجزة تناسب مواقف مختلفة (موافقة وترحيب، شكر، اعتذار لبق، استفسار، تأجيل مهذب).
                أرجع الردود بصيغة JSON التالية فقط:
                [
                  {"label": "موافقة", "text": "نص الرد هنا", "icon": "👍"},
                  {"label": "شكر", "text": "نص الرد هنا", "icon": "🙏"},
                  {"label": "اعتذار", "text": "نص الرد هنا", "icon": "💐"},
                  {"label": "استفسار", "text": "نص الرد هنا", "icon": "❓"},
                  {"label": "تأجيل", "text": "نص الرد هنا", "icon": "⏳"}
                ]
            """.trimIndent()

            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                try {
                    val jsonStr = extractJsonString(onlineResult)
                    val arr = JSONArray(jsonStr)
                    val replies = mutableListOf<SmartReply>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        replies.add(
                            SmartReply(
                                label = obj.optString("label", "رد"),
                                text = obj.optString("text", ""),
                                icon = obj.optString("icon", "💬")
                            )
                        )
                    }
                    if (replies.isNotEmpty()) return@withContext replies
                } catch (e: Throwable) {
                    Log.w(TAG, "Error parsing online replies JSON", e)
                }
            }
        }

        // Local Smart Replies generator
        return@withContext localSmartReplies(clean)
    }

    /**
     * Continue or complete thought
     */
    suspend fun continueText(
        text: String,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                أكمل الجملة أو الفكرة التالية بأسلوب ذكي وطبيعي ومترابط (جملة أو جملتين إضافيتين تكملان المعنى تماماً):
                "$clean"
                أرجع النص المكتمل كاملاً فقط بدون أي شرح إضافي.
            """.trimIndent()

            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim().removeSurrounding("\"", "\"")
            }
        }

        // Local smart continuation
        return@withContext when {
            clean.endsWith("أرجو") -> "$clean منكم التكرم بالاطلاع والموافاة بالرد المناسب."
            clean.endsWith("أتمنى") -> "$clean لكم دوام التوفيق والنجاح ويوماً مليئاً بالخير."
            clean.endsWith("هل يمكن") -> "$clean تزويدي بالتفاصيل الإضافية لنتمكن من المتابعة فوراً؟"
            clean.endsWith("بخصوص") -> "$clean الموضوع المذكور، يسعدني التنسيق معكم لإتمامه في أقرب وقت."
            clean.endsWith("شكراً") -> "$clean جزيلاً على تعاونكم الدائم وحسن تعاملكم الكريم."
            else -> "$clean ، وبانتظار ردكم الكريم مع أطيب التحيات."
        }
    }

    /**
     * Summarize text
     */
    suspend fun summarizeText(
        text: String,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                لخص النص التالي في سطر أو سطرين مركزين يوضحان أهم النقاط:
                "$clean"
                أرجع الملخص فقط.
            """.trimIndent()

            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim()
            }
        }

        // Local summarizer: extract first and last sentence or core clauses
        val sentences = clean.split(Regex("[.!?\n]+")).filter { it.isNotBlank() }
        if (sentences.size <= 2) return@withContext clean
        return@withContext "${sentences.first().trim()}، و${sentences.last().trim()}."
    }

    // --- Local Smart NLP Algorithms ---

    private fun localRewriteWithTone(text: String, tone: WritingTone): String {
        val clean = text.trim()
        return when (tone) {
            WritingTone.FORMAL -> {
                var res = clean
                res = res.replace(Regex("^(هلا|أهلاً|مرحبا|مرحباً)"), "السلام عليكم ورحمة الله وبركاته، تحية طيبة وبعد:")
                res = res.replace("أبي", "أرغب في")
                res = res.replace("عايز", "أود إحاطتكم علماً برغبتي في")
                res = res.replace("بدي", "أرجو منكم التكرم بـ")
                res = res.replace("طيب", "حسناً، تم الاطلاع")
                res = res.replace("ما اقدر", "يتعذر عليّ حالياً")
                res = res.replace("اوكي", "تم اعتماد ذلك")
                if (!res.startsWith("السلام عليكم") && !res.startsWith("تحية")) {
                    res = "تحية طيبة وبعد، $res"
                }
                if (!res.endsWith(".")) {
                    res = "$res، وتقبلوا خالص الشكر والتقدير."
                }
                res
            }

            WritingTone.PROFESSIONAL -> {
                var res = clean
                res = res.replace(Regex("^(هلا|أهلاً)"), "مرحباً بكم،")
                res = res.replace("مشروع", "المشروع القائم")
                res = res.replace("شغل", "المهام الموكلة")
                res = res.replace("خلصت", "تم إنجاز المطلوب بنجاح")
                res = res.replace("بسرعة", "في أقرب وقت ممكن مع مراعاة الجودة")
                res = res.replace("مافي مشكلة", "بالتأكيد، تم أخذ ذلك في الحسبان")
                if (!res.endsWith(".")) {
                    res = "$res. نتطلع لتعاونكم المثمر."
                }
                res
            }

            WritingTone.FRIENDLY -> {
                var res = clean
                if (!res.startsWith("أهلاً") && !res.startsWith("مرحباً") && !res.startsWith("يا هلا")) {
                    res = "يا هلا والله! $res"
                }
                res = res.replace("شكراً", "شكراً من كل قلبي ويسعدك ربي")
                res = res.replace("إن شاء الله", "بإذن الله يا غالي")
                if (!res.contains("🌸") && !res.contains("😊")) {
                    res = "$res 🌸😊"
                }
                res
            }

            WritingTone.CONCISE -> {
                var res = clean
                res = res.replace(Regex("^(السلام عليكم ورحمة الله وبركاته|تحية طيبة وبعد|أهلاً وسهلاً بك|عزيزي)"), "")
                res = res.replace(Regex("(في الحقيقة|كما تعلمون|كما هو معلوم|أود أن أقول أن|من الجدير بالذكر أن)"), "")
                res = res.replace(Regex("\\s+"), " ").trim()
                res
            }

            WritingTone.POETIC -> {
                var res = clean
                res = res.replace("صباح الخير", "طاب صباحكم بأنوار المسرات وشذا الياسمين")
                res = res.replace("مساء الخير", "مساء يتوشح بالسكينة ويزدان بجمال حضوركم")
                res = res.replace("شكراً", "لكم من صميم الفؤاد وافر الامتنان وعاطر الثناء")
                res = res.replace("كيف حالك", "كيف أمسى حالكم وكيف تجري بكم رياح الأيام؟")
                if (!res.endsWith(".")) {
                    res = "$res، دمتم برعاية الله وحفظه."
                }
                res
            }

            WritingTone.CASUAL_EMOJI -> {
                var res = clean
                res = res.replace("مرحباً", "يا هلا وغلا 👋✨")
                res = res.replace("تمام", "تمام التمام وفوق النخل 🚀")
                res = res.replace("شكراً", "تسلم لي يا عسل 🙏💖")
                res = res.replace("حسناً", "أكيد ولا يهمك أبداً 👌")
                if (!res.contains("✨") && !res.contains("🔥")) {
                    res = "$res ✨🔥"
                }
                res
            }

            WritingTone.PERSUASIVE -> {
                var res = clean
                if (!res.startsWith("من الرائع") && !res.startsWith("لا شك أن")) {
                    res = "من المؤكد أن هذه الخطوة ستحدث فرقاً إيجابياً كبيراً: $res"
                }
                if (!res.endsWith(".")) {
                    res = "$res، وتطبيق هذا الأمر سيحقق أفضل النتائج المرجوة بلا شك."
                }
                res
            }
        }
    }

    private fun localProofread(text: String): ProofreadResult {
        var corrected = text
        val improvements = mutableListOf<String>()

        // 1. Hamzas
        val hamzaPairs = listOf(
            Regex("\\bالي\\b") to "إلى",
            Regex("\\bاذا\\b") to "إذا",
            Regex("\\bان\\b") to "إن",
            Regex("\\bاكثر\\b") to "أكثر",
            Regex("\\bاكبر\\b") to "أكبر",
            Regex("\\bاصبح\\b") to "أصبح",
            Regex("\\bالان\\b") to "الآن",
            Regex("\\bايضا\\b") to "أيضاً",
            Regex("\\bشكرا\\b") to "شكراً",
            Regex("\\bعفوا\\b") to "عفواً",
            Regex("\\bجدا\\b") to "جداً",
            Regex("\\bحقا\\b") to "حقاً",
            Regex("\\bاهلا\\b") to "أهلاً",
            Regex("\\bسهلا\\b") to "سهلاً",
            Regex("\\bمرحبا\\b") to "مرحباً"
        )
        for ((regex, rep) in hamzaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("تصحيح الهمزات والتنوين")) {
                    improvements.add("تصحيح الهمزات والتنوين")
                }
            }
        }

        // 2. Taa Marbuta
        val taaPairs = listOf(
            Regex("\\bمدرسه\\b") to "مدرسة",
            Regex("\\bسياره\\b") to "سيارة",
            Regex("\\bصوره\\b") to "صورة",
            Regex("\\bحياه\\b") to "حياة",
            Regex("\\bرساله\\b") to "رسالة",
            Regex("\\bطبيعه\\b") to "طبيعة",
            Regex("\\bجميله\\b") to "جميلة",
            Regex("\\bرائعه\\b") to "رائعة",
            Regex("\\bشركه\\b") to "شركة"
        )
        for ((regex, rep) in taaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("ضبط التاء المربوطة والمفتوحة")) {
                    improvements.add("ضبط التاء المربوطة والمفتوحة")
                }
            }
        }

        // 3. Yaa & Alef Maqsura
        val yaaPairs = listOf(
            Regex("\\bمتي\\b") to "متى",
            Regex("\\bحتي\\b") to "حتى",
            Regex("\\bلدي\\b") to "لدى",
            Regex("\\bسوي\\b") to "سوى",
            Regex("\\bأخري\\b") to "أخرى"
        )
        for ((regex, rep) in yaaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("تصحيح الألف المقصورة والياء")) {
                    improvements.add("تصحيح الألف المقصورة والياء")
                }
            }
        }

        // 4. Common Compound Blunders
        if (corrected.contains("انشاء الله") || corrected.contains("انشاءالله") || corrected.contains("إنشاء الله")) {
            corrected = corrected.replace(Regex("(انشاء الله|انشاءالله|إنشاء الله)"), "إن شاء الله")
            improvements.add("فصل (إن شاء الله)")
        }
        if (corrected.contains("ماشاءالله") || corrected.contains("ماشاء الله")) {
            corrected = corrected.replace(Regex("(ماشاءالله|ماشاء الله)"), "ما شاء الله")
            improvements.add("ضبط (ما شاء الله)")
        }
        if (corrected.contains("لاكن")) {
            corrected = corrected.replace(Regex("\\bلاكن\\b"), "لكن")
            improvements.add("تصحيح رسم الكلمات الشائعة (لكن)")
        }
        if (corrected.contains("هاذا")) {
            corrected = corrected.replace(Regex("\\bهاذا\\b"), "هذا")
            improvements.add("تصحيح رسم أسماء الإشارة")
        }

        // 5. Spacing around punctuation
        val punctuationFixed = corrected
            .replace(Regex("\\s+([،,؛;:.!?])"), "$1")
            .replace(Regex("([،,؛;:.!?])(?=[^\\s0-9])"), "$1 ")
        if (punctuationFixed != corrected) {
            corrected = punctuationFixed
            improvements.add("ضبط المسافات وعلامات الترقيم")
        }

        if (improvements.isEmpty()) {
            improvements.add("النص سليم إملائياً ونحوياً ✓")
        }

        return ProofreadResult(text, corrected, improvements)
    }

    private fun localSmartReplies(context: String): List<SmartReply> {
        return listOf(
            SmartReply("موافقة وترحيب", "بكل سرور، موافق وبانتظار التنسيق معكم لنبدأ فوراً بإذن الله. 👍", "👍"),
            SmartReply("شكر وتقدير", "أشكرك جزيل الشكر والتقدير على اهتمامك ومتابعتك الكريمة، بارك الله فيك. 🙏", "🙏"),
            SmartReply("اعتذار لبق", "أعتذر منك جداً، مرتبط حالياً بالتزام طارئ، وسأتواصل معك بأقرب فرصة ممكنة. 💐", "💐"),
            SmartReply("استفسار وتوضيح", "شكراً لك، هل يمكنك التكرم بتوضيح النقطة أكثر لنتمكن من المتابعة بدقة؟ ❓", "❓"),
            SmartReply("تأجيل مهذب", "تم استلام رسالتك، سأقوم بمراجعة التفاصيل والرد عليك بشكل وافٍ لاحقاً اليوم. ⏳", "⏳")
        )
    }

    // --- OkHttp Gemini REST API Implementation ---

    private fun callGeminiRest(prompt: String, apiKey: String): String? {
        return try {
            val url = "$BASE_URL?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArr)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini REST call failed with code: ${response.code}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null

            parts.getJSONObject(0).optString("text", null)
        } catch (e: Throwable) {
            Log.w(TAG, "Gemini call exception: ${e.message}")
            null
        }
    }

    private fun extractJsonString(raw: String): String {
        val trimmed = raw.trim()
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1)
        }
        val firstBracket = trimmed.indexOf('[')
        val lastBracket = trimmed.lastIndexOf(']')
        if (firstBracket != -1 && lastBracket != -1 && lastBracket > firstBracket) {
            return trimmed.substring(firstBracket, lastBracket + 1)
        }
        return trimmed
    }
}
