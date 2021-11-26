package rxcats.chatservice.domain

enum class SystemMessageType {
    LoginSucceeded, Disconnected, OnError,
    JoinLobby, LeaveLobby, LobbyUsers, LobbyChat, LobbyRooms,
    JoinRoomSucceeded, LeaveRoomSucceeded, JoinRoom, LeaveRoom, RoomUsers, RoomChat
}