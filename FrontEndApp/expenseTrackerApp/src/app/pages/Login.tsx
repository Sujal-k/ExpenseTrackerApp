import {View, Text, StyleSheet} from 'react-native';
import React, {useEffect, useState} from 'react';
import CustomText from '../components/CustomText';
import CustomBox from '../components/CustomBox';
import {Button, ButtonText} from '@gluestack-ui/themed';
import { TextInput } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Login = ({navigation}) => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [loggedIn, setLoggedIn] = useState(false);

  const isLoggedIn = async () => {
    const accessToken = await AsyncStorage.getItem('accessToken');
    console.log('Stored accessToken:', accessToken);
    const response = await fetch('http://192.168.203.248:9898/ping', {
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + accessToken,
        'X-Requested-With': 'XMLHttpRequest',
      },
    });
  
    if (response.ok) {
      return true;
    } else if (response.status === 401) {
      console.log('Access token expired. Attempting refresh...');
      const refreshed = await refreshToken();
      if (refreshed) {
        // retry ping with new token
        const newAccessToken = await AsyncStorage.getItem('accessToken');
        const retryResponse = await fetch('http://192.168.203.248:9898/ping', {
          method: 'GET',
          headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + newAccessToken,
            'X-Requested-With': 'XMLHttpRequest',
          },
        });
        return retryResponse.ok;
      }
    }
    return false;
  };
  
  const refreshToken = async () => {

    console.log('Inside Refresh token');
    const refreshToken = await AsyncStorage.getItem('refreshToken');
    const response = await fetch(`http://192.168.203.248:9898/auth/v1/refreshToken`, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
      },
      body: JSON.stringify({
        token: refreshToken,
      }),
    });
    console.log('Refresh Token Status:', response.status);
    // const rawText = await response.text();
// console.log('Refresh Token Raw Response:', rawText);
    if (response.ok) {
      const data = await response.json();
      console.log('Refresh Token New Response:', data);
      await AsyncStorage.setItem('accessToken', data['accessToken']);
      await AsyncStorage.setItem('refreshToken', data['token']);

      console.log(
        'Tokens after refresh are ' + data['token'] + ' ' + data['accessToken']
      );

      // const refreshToken = await AsyncStorage.getItem('refreshToken');
      // const accessToken = await AsyncStorage.getItem('accessToken');
      // console.log(
      //   'Tokens after refresh are ' + refreshToken + ' ' + accessToken,
      // );
    }

    return response.ok;
  };

  const gotoHomePageWithLogin = async () => {
    try {
      const response = await fetch(`http://192.168.203.248:8080/auth/v1/login`, {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
          'X-Requested-With': 'XMLHttpRequest',
        },
        body: JSON.stringify({
          username: userName,
          password: password,
        }),
      });
  
      if (response.ok) {
        
        const data = await response.json();
        console.log('Refresh Token Raw Response:', data);
        await AsyncStorage.setItem('refreshToken', data['token']);
        await AsyncStorage.setItem('accessToken', data['accessToken']);
        navigation.navigate('Home', { name: 'Home' });
      } else {
        const error = await response.text();
        console.warn('Login failed:', response.status, error);
        alert('Login failed: ' + error);
      }
    } catch (err) {
      console.error('Login exception:', err);
      alert('Login error: ' + err.message);
    }
  };
  

  const gotoSignUp = () =>{
    navigation.navigate('SignUp', {name:'SignUp'});
  }
  const [checkingLogin, setCheckingLogin] = useState(true);

useEffect(() => {
  const handleLogin = async () => {
    try {
      const loggedIn = await isLoggedIn();
      setLoggedIn(loggedIn);
      if (loggedIn) {
        navigation.navigate('Home', {name: 'Home'});
      }
    } finally {
      setCheckingLogin(false);
    }
  };
  handleLogin();
}, []);
if (checkingLogin) {
  return (
    <View style={styles.loginContainer}>
      <Text>🔒 Checking session...</Text>
    </View>
  );
}

  return (
      <View style={styles.loginContainer}>
        <CustomBox style={loginBox}>
          <CustomText style={styles.heading}>Login</CustomText>
          <TextInput 
            placeholder="User Name"
            value={userName}
            onChangeText={text => setUserName(text)}
            style={styles.textInput}
            placeholderTextColor="#888"
          />
          <TextInput
            placeholder="Password"
            value={password}
            onChangeText={text => setPassword(text)}
            style={styles.textInput}
            placeholderTextColor="#888"
            secureTextEntry
          />
        </CustomBox>
        <Button onPressIn ={()=> gotoHomePageWithLogin()} style={styles.button}>
          <CustomBox  style={buttonBox}>
            <CustomText style={{textAlign: 'center'}}>Submit</CustomText>
          </CustomBox>
        </Button>
        <Button onPressIn ={()=> gotoSignUp()} style={styles.button}>
          <CustomBox style={buttonBox}>
            <CustomText style={{textAlign: 'center'}}>Goto Signup</CustomText>
          </CustomBox>
        </Button>
      </View>
  );
};

export default Login;

const styles = StyleSheet.create({
  loginContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  button: {
    marginTop: 20,
    width: '30%',
  },
  heading: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  textInput: {
    backgroundColor: '#f0f0f0',
    borderRadius: 5,
    padding: 10,
    marginBottom: 10,
    width: '100%',
    color: 'black',
  },
});

const loginBox = {
  mainBox: {
    backgroundColor: '#fff',
    borderColor: 'black',
    borderWidth: 1,
    borderRadius: 10,
    padding: 20,
  },
  shadowBox: {
    backgroundColor: 'gray',
    borderRadius: 10,
  },
};

const buttonBox = {
  mainBox: {
    backgroundColor: '#fff',
    borderColor: 'black',
    borderWidth: 1,
    borderRadius: 10,
    padding: 10,
  },
  shadowBox: {
    backgroundColor: 'gray',
    borderRadius: 10,
  },
};

