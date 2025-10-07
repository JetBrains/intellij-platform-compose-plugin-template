package org.jetbrains.plugins.template.chatApp.repository

class AIResponseGenerator {

    fun generateAIResponse(userMessage: String): String {
        val message = userMessage.lowercase()

        return when {
            message.contains("hello") || message.contains("hi") || message.contains("hey") -> {
                listOf(
                    "Hello! How can I assist you today?",
                    "Hi there! What's on your mind?",
                    "Hey! Great to hear from you. How are things going?",
                    "Hello! I'm here to help with any questions you might have."
                ).random()
            }

            message.contains("how are you") || message.contains("how's it going") -> {
                listOf(
                    "I'm doing well, thank you for asking! How about yourself?",
                    "Everything's running smoothly on my end. How are you doing?",
                    "I'm here and ready to help! How has your day been?",
                    "All systems operational! What brings you here today?"
                ).random()
            }

            message.contains("help") || message.contains("assist") -> {
                listOf(
                    "I'd be happy to help! What do you need assistance with?",
                    "Of course! What can I help you figure out?",
                    "I'm here to assist. Could you tell me more about what you need?",
                    "Absolutely! What kind of help are you looking for?"
                ).random()
            }

            message.contains("thank") -> {
                listOf(
                    "You're very welcome! Happy to help anytime.",
                    "My pleasure! Is there anything else I can assist with?",
                    "Glad I could help! Feel free to ask if you need anything else.",
                    "You're welcome! That's what I'm here for."
                ).random()
            }

            message.contains("code") || message.contains("programming") -> {
                listOf(
                    "I love talking about code! What programming topic interests you?",
                    "Programming is fascinating! Are you working on a specific project?",
                    "Code-related questions are my specialty. What would you like to know?",
                    "Great choice of topic! What programming language are you using?"
                ).random()
            }

            message.contains("?") -> {
                listOf(
                    "That's a great question! Let me think about that...",
                    "Interesting question! From my perspective, I'd say...",
                    "Good point! Here's how I see it:",
                    "That's worth exploring! Based on what I know..."
                ).random()
            }

            message.length > 100 -> {
                listOf(
                    "That's quite detailed! I appreciate you sharing all that context.",
                    "Thanks for the comprehensive explanation. That gives me a lot to work with!",
                    "Wow, you've really thought this through! Let me process all of that...",
                    "I can see you've put a lot of thought into this. Here's my take:"
                ).random()
            }

            else -> {
                listOf(
                    "That's an interesting point! Could you tell me more about your perspective?",
                    "I see what you're getting at. That's definitely worth considering.",
                    "Fascinating! I hadn't thought about it from that angle before.",
                    "Good observation! What made you think of that?",
                    "That's insightful! How did you come to that conclusion?",
                    "I appreciate you sharing that thought with me.",
                    "That's a unique way to look at it! I like your thinking.",
                    "You raise an excellent point there!",
                    "That's definitely food for thought. Very interesting!",
                    "I find your perspective quite compelling. Tell me more!"
                ).random()
            }
        }
    }
}