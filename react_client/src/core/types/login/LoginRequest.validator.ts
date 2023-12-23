import * as yup from 'yup';

export const LoginRequestValidator = yup.object().shape({
  email: yup.string().email('Email must be correct').required('Email field is required'),
  password: yup.string().required('Password is required')
});
