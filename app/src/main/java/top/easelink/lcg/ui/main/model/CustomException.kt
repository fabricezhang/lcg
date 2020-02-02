package top.easelink.lcg.ui.main.model

class BlockException(val alertMessage: String) : Exception()

class NetworkException : Exception()

class LoginRequiredException: Exception()