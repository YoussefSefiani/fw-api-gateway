@SpringBootTest
class UserServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void init() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void givenUserId_whenUserExists_ThenReturnUserOptional() {
        User user = new User(5L, "testuser", "password", true, "USER");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUser(5L));
    }

    @Test
    public void givenUserId_whenUserIdNotFound_ThenThrowIllegalStateException() {
        when(userRepository.findById(543L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.getUser(543L));
    }

    @Test
    public void givenUser_WhenRegisterSuccessful_ThenReturnUser() {
        User user = new User(null, "testuser", "password", true, "USER");
        String encryptedPassword = "encryptedpassword";

        when(userRepository.findByUsernameEqualsIgnoreCase(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encryptedPassword);

        User registeredUser = userService.registerUser(user);

        assertEquals(encryptedPassword, registeredUser.getPassword());
        assertNotEquals(null, registeredUser);
    }

    @Test
    public void givenUser_WhenUsernameAlreadyTaken_ThenThrowIllegalStateException() {
        User user = new User(null, "testuser", "password", true, "USER");

        when(userRepository.findByUsernameEqualsIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        assertThrows(ResponseStatusException.class, () -> userService.registerUser(user));
    }

    @Test
    public void givenUsernameAndPassword_WhenUsernameAndPasswordUpdateSuccessful_ThenReturnStatus200() throws JSONException {
        User user = new User(12L, "testuser", "password", true, "USER");
        User updatedUser = new User(12L, "updatedusername", "updatedpassword", true, "USER");

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        userService.updateUser(12L, updatedUser);

        assertEquals("updatedusername", user.getUsername());
        assertEquals(passwordEncoder.encode("updatedpassword"), user.getPassword());

    }

    @Test
    public void givenUsername_WhenUsernameUpdateSuccessful_ThenReturnStatus200() {
        User user = new User(12L, "testuser", "password", true, "USER");
        User updatedUser = new User(12L, "updateduser", "password", true, "USER");

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        userService.updateUser(12L, updatedUser);

        assertEquals("updateduser", user.getUsername());

    }

    @Test
    public void givenPassword_WhenPasswordUpdateSuccessful_ThenReturnStatus200() {
        User user = new User(12L, "testuser", "password", true, "USER");
        User updatedUser = new User(12L, "test", "updatedpassword", true, "USER");

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        userService.updateUser(12L, updatedUser);

        assertEquals(passwordEncoder.encode("updatedpassword"), user.getPassword());

    }

    @Test
    public void givenUsername_WhenUsernameAlreadyExists_ThenThrowIllegalStateException() {
        User updatedUser = new User(12L, "testuser", "password", true, "USER");

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(12L, updatedUser));
    }

    @Test
    public void givenUserId_WhenUserIdNotFound_ThenThrowIllegalStateException() {
        User updatedUser = new User(12L, "testuser", "password", true, "USER");
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(567L, updatedUser));
    }


    @Test
    public void givenUserId_whenDeleteSuccessful_ThenReturnStatus200() {
        when(userRepository.existsById(5L)).thenReturn(true);
        userService.deleteUser(5L);
        verify(userRepository).deleteById(5L);
    }

    @Test
    public void givenUserId_whenIdOfUserToDeleteNotFound_ThenThrowIllegalStateException() {
        when(userRepository.existsById(567L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(567L));
    }

}