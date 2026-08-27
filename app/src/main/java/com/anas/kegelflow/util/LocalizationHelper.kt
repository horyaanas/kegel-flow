package com.anas.kegelflow.util

import com.anas.kegelflow.data.AppLanguage

object LocalizationHelper {

    fun getString(key: String, language: AppLanguage): String {
        val isAr = language == AppLanguage.ARABIC
        return when (key) {
            "app_title" -> if (isAr) "تمارين كيجل" else "Kegel Flow"
            "app_tagline" -> if (isAr) "تطوير العضلات وإعادة التوازن" else "Strengthen & Rebalance"
            "greeting" -> if (isAr) "أهلاً بك 👋" else "Welcome 👋"
            "ready_prompt" -> if (isAr) "جاهز للجلسة اليومية؟" else "Ready for today's session?"
            "today_workout" -> if (isAr) "تمرين اليوم" else "Today's Workout"
            "start_workout" -> if (isAr) "ابدأ التمرين الان" else "Start Workout Now"
            "quick_stats" -> if (isAr) "إحصائيات السريعة" else "Quick Stats"
            "today_sessions" -> if (isAr) "جلسات اليوم" else "Today's Sessions"
            "total_sessions" -> if (isAr) "إجمالي الجلسات" else "Total Sessions"
            "streak_days" -> if (isAr) "الأيام المتتالية" else "Day Streak"
            "days" -> if (isAr) "أيام" else "Days"
            "sessions" -> if (isAr) "جلسات" else "Sessions"
            "mins" -> if (isAr) "دقيقة" else "Mins"
            
            // Tabs
            "tab_home" -> if (isAr) "الرئيسية" else "Home"
            "tab_plans" -> if (isAr) "التمارين" else "Plans"
            "tab_stats" -> if (isAr) "الإحصائيات" else "Stats"
            "tab_settings" -> if (isAr) "الإعدادات" else "Settings"
            
            // Workout Stages
            "stage_preparing" -> if (isAr) "استعد..." else "Prepare..."
            "stage_contract" -> if (isAr) "اضغط — Contract" else "Contract — اضغط"
            "stage_relax" -> if (isAr) "استرخِ — Relax" else "Relax — استرخِ"
            "stage_rest" -> if (isAr) "استراحة" else "Rest"
            "stage_finished" -> if (isAr) "أحسنت! أكملت الجلسة" else "Great Job! Session Complete"
            "breathe_naturally" -> if (isAr) "تنفس بشكل طبيعي وتجنب حبس النفس" else "Breathe naturally & don't hold your breath"
            "rep_counter" -> if (isAr) "تكرار %d من %d" else "Rep %d of %d"
            "pause" -> if (isAr) "إيقاف مؤقت" else "Pause"
            "resume" -> if (isAr) "استئناف" else "Resume"
            "skip" -> if (isAr) "تخطي المرحلة" else "Skip Stage"
            "quit" -> if (isAr) "إنهاء الجلسة" else "Quit Workout"
            
            // Dynamic Workout Instructions
            "instruction_prep_title" -> if (isAr) "استعد للبدء" else "Get Ready"
            "instruction_prep_sub" -> if (isAr) "اتخذ وضعية مريحة (جلوس أو استلقاء) وركز ذهنك على عضلات الحوض" else "Take a comfortable position and focus on your pelvic floor muscles"
            "instruction_contract_title" -> if (isAr) "اشدد للأعلى وللداخل" else "Squeeze & Lift Inward"
            "instruction_contract_sub" -> if (isAr) "اسحب عضلات قاع الحوض للأعلى، وحافظ على استرخاء البطن والفخذين وتنفس بهدوء" else "Lift pelvic floor muscles up; keep abdomen and thighs relaxed, breathe gently"
            "instruction_relax_title" -> if (isAr) "أرخِ العضلات تماماً" else "Release & Fully Relax"
            "instruction_relax_sub" -> if (isAr) "دع التوتر يتلاشى بالكامل؛ الاسترخاء ضروري جداً لبناء قوة ومرونة العضلات" else "Let go of all tension completely; full relaxation is vital for muscle elasticity"
            "instruction_rest_title" -> if (isAr) "استراحة قصيرة" else "Short Rest"
            "instruction_rest_sub" -> if (isAr) "تنفس بعمق واستعد للتكرار التالي" else "Breathe deeply and prepare for the next rep"
            
            // Educational Guide & Anatomy
            "guide_title" -> if (isAr) "الدليل الطبي لتمارين كيجل" else "Pelvic Floor Exercise Guide"
            "guide_banner_title" -> if (isAr) "كيف تؤدي التمرين بشكل صحيح؟" else "How to do Kegel exercises properly?"
            "guide_banner_sub" -> if (isAr) "شرح تشريحي وإرشادات هامة لأفضل نتيجة" else "Anatomical guide & key tips for best results"
            "guide_tab_steps" -> if (isAr) "خطوات الأداء" else "Exercise Steps"
            "guide_tab_anatomy" -> if (isAr) "العضلات المستهدفة" else "Target Muscles"
            "guide_tab_mistakes" -> if (isAr) "أخطاء شائعة" else "Common Mistakes"
            
            "anatomy_title" -> if (isAr) "عضلات قاع الحوض (عضلات كيجل)" else "Pelvic Floor Musculature"
            "anatomy_sub" -> if (isAr) "شبكة عضلية تشبه الأرجوحة تمتد من عظم العانة في الأمام إلى العصعص في الخلف، تسند المثانة والأمعاء وتتحكم في تدفق البول واستقرار الحوض." else "A hammock-like muscular sling extending from the pubic bone to the tailbone, supporting bladder & bowel and controlling stability."
            "anatomy_label_bladder" -> if (isAr) "المثانة" else "Bladder"
            "anatomy_label_pelvic_floor" -> if (isAr) "عضلات قاع الحوض" else "Pelvic Floor Muscles"
            "anatomy_label_pubic_bone" -> if (isAr) "عظم الحوض" else "Pelvic Support"
            "anatomy_tip" -> if (isAr) "💡 عند الشد الصحيح: ستشعر بحركة رفع وسحب للأعلى نحو السرة دون حركة الفخذين أو المؤخرة." else "💡 Proper Squeeze: You should feel an upward lift toward the navel without moving buttocks or thighs."

            "guide_step1_title" -> if (isAr) "1. تحديد العضلة الصحيحة" else "1. Identify the Right Muscle"
            "guide_step1_desc" -> if (isAr) "لتحديد العضلة بدقة: حاول إيقاف تدفق البول للحظة أثناء التبول للتعرف على الإحساس فقط (لا تكرر هذا أثناء التبول الفعلي لتجنب احتباس البول)." else "To locate muscles: Briefly pause urine stream to feel the squeeze (do this only once for testing, never make it a habit while urinating)."
            
            "guide_step2_title" -> if (isAr) "2. تقنية الشد والسحب" else "2. Contract & Lift Technique"
            "guide_step2_desc" -> if (isAr) "تخيل أنك تسحب العضلات وترفعها إلى الداخل وللأعلى. حافظ على ثبات واستقرار عضلات البطن والأرداف والفخذين تماماً." else "Imagine pulling and lifting the muscles inward and upward. Keep your stomach, glutes, and thighs completely relaxed."
            
            "guide_step3_title" -> if (isAr) "3. التنفس الطبيعي والحر" else "3. Smooth Continuous Breathing"
            "guide_step3_desc" -> if (isAr) "تجنب حبس نفسك إطلاقاً أثناء الشد. تنفس بسلاسة؛ يمكنك أخذ شهيق عميق قبل الشد وإخراج الزفير أثناء الشد بهدوء." else "Never hold your breath while squeezing. Breathe freely and gently in and out throughout each repetition."
            
            "guide_step4_title" -> if (isAr) "4. الاسترخاء المتكافئ" else "4. Equal Complete Relaxation"
            "guide_step4_desc" -> if (isAr) "الاسترخاء بين كل تكرار لا يقل أهمية عن الشد؛ فهو يمنع تشنج وإجهاد العضلات ويسمح لها باستعادة الطاقة وتجديد المرونة." else "Relaxation between reps is just as crucial as the contraction; it prevents muscle cramps and rebuilds elastic strength."

            "mistake1_title" -> if (isAr) "حبس الأنفاس" else "Holding Your Breath"
            "mistake1_desc" -> if (isAr) "يزيد الضغط داخل البطن ويقلل فاعلية التمرين. احرص على التنفس بانتظام." else "Increases intra-abdominal pressure and reduces effectiveness. Breathe normally."
            
            "mistake2_title" -> if (isAr) "الدفع للأسفل بدلاً من السحب" else "Pushing Downward"
            "mistake2_desc" -> if (isAr) "الدفع يضعف العضلات؛ الحركة الصحيحة دائماً هي الرفع والسحب للأعلى." else "Bearing down strains the pelvic floor; the movement must always be an upward lift."
            
            "mistake3_title" -> if (isAr) "شد الفخذين أو الأرداف" else "Tightening Thighs or Buttocks"
            "mistake3_desc" -> if (isAr) "هذا يعوض عن العضلات المستهدفة ولا يفيدها. ضع يدك على بطنك للتأكد من استرخائه." else "This uses compensatory muscles instead. Place a hand on your belly to ensure it stays soft."

            "disclaimer_notice" -> if (isAr) "تنبيه هام: هذا التطبيق أداة تدريبية وتذكيرية لتحسين نمط الحياة وليس أداة تشخيص أو علاج طبي. يُنصح باستشارة الطبيب أو أخصائي العلاج الطبيعي عند وجود أي أعراض طبية." else "Important: This app is an exercise guide and habit tracker, not a medical device. Consult a physician or physical therapist for clinical conditions."
            
            // Custom Plan
            "custom_plan" -> if (isAr) "خطة مخصصة" else "Custom Plan"
            "create_custom_plan" -> if (isAr) "إنشاء خطة مخصصة" else "Create Custom Plan"
            "plan_name" -> if (isAr) "اسم الخطة" else "Plan Name"
            "contract_time" -> if (isAr) "مدة الضغط (ثوانٍ)" else "Contract Time (sec)"
            "relax_time" -> if (isAr) "مدة الاسترخاء (ثوانٍ)" else "Relax Time (sec)"
            "reps_count" -> if (isAr) "عدد التكرارات" else "Repetitions"
            "save" -> if (isAr) "حفظ" else "Save"
            "cancel" -> if (isAr) "إلغاء" else "Cancel"
            "select_plan" -> if (isAr) "تحديد هذه الخطة" else "Select This Plan"
            
            // Settings
            "language" -> if (isAr) "اللغة" else "Language"
            "theme" -> if (isAr) "المظهر" else "Theme"
            "sound" -> if (isAr) "الصوت والتنبيهات" else "Sound Effects"
            "vibration" -> if (isAr) "الاهتزاز عند الانتقال" else "Vibration Feedback"
            "prep_time" -> if (isAr) "مدة العد التنازلي قبل بدء التمرين" else "Preparation Countdown"
            "reminders" -> if (isAr) "التذكيرات اليومية" else "Daily Reminders"
            "add_reminder" -> if (isAr) "إضافة تذكير جديد" else "Add New Reminder"
            "reset_stats" -> if (isAr) "إعادة ضبط الإحصائيات" else "Reset Statistics"
            "confirm_reset" -> if (isAr) "هل أنت تأكد من إرجاع الإحصائيات للصفر؟" else "Are you sure you want to reset all stats?"
            
            else -> key
        }
    }
}
