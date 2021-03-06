/*
 * Copyright 2017 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mk.gdx.firebase.distributions;

import mk.gdx.firebase.auth.GdxFirebaseUser;
import mk.gdx.firebase.callbacks.AuthCallback;

/**
 * Provides access to Firebase authorization methods.
 * <p>
 * You can read about possible Firebase authorization methods in Firebase docs:
 * <p>
 *
 * @see <a href="https://firebase.google.com/docs/auth/android/start">android firebase docs</a>
 * @see <a href="https://firebase.google.com/docs/auth/ios/start">ios firebase docs</a>
 */
public interface AuthDistribution
{

    /**
     * Gets currently logged in user or null if logged in user does not exists.
     *
     * @return Firebase user wrapped by {@link GdxFirebaseUser} class, may by null.
     */
    GdxFirebaseUser getCurrentUser();

    /**
     * Registers new user and gives response by {@code AuthCallback}.
     *
     * @param email    New email address
     * @param password New password
     * @param callback Authorization callback, cant be null.
     * @see AuthCallback
     */
    void createUserWithEmailAndPassword(String email, char[] password, AuthCallback callback);

    /**
     * Signs in into application and gives response by {@code AuthCallback}
     * <p>
     * If something is wrong {@link AuthCallback#onFail(Exception)} will be call.
     *
     * @param email    Firebase user email
     * @param password Firebase user password
     * @param callback Authorization callback, cant be null.
     * @see AuthCallback
     */
    void signInWithEmailAndPassword(String email, char[] password, AuthCallback callback);

    /**
     * Signs in into application by token and gives response by {@code AuthCallback}.
     * <p>
     * More info you about custom tokens you can find <a href="https://firebase.google.com/docs/auth/admin/create-custom-tokens">here</a>.
     *
     * @param token    Custom token from your firebase console.
     * @param callback Authorization callback, cant be null.
     * @see AuthCallback
     */
    void signInWithToken(String token, AuthCallback callback);

    /**
     * Signs in into application anonymously and gives response by {@code AuthCallback}.
     * <p>
     * It may be very useful when you do not provide user authentication in your application and<p>
     * working with {@link mk.gdx.firebase.GdxFIRDatabase} or with {@link mk.gdx.firebase.GdxFIRStorage}<p>
     * because default Firebase requires authorization for database and storage actions.
     *
     * @param callback Authorization callback, cant be null.
     */
    void signInAnonymously(AuthCallback callback);
}
