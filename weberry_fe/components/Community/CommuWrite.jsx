import React, { useEffect, useRef, useState } from 'react'
import ErrorMessage from '../Error/ErrorMessage';

const CommuWrite = (props) => {
  const setToWritePost = props.setToWritePost;
  const toWritePost = props.toWritePost;
  const token = props.token;

  const ref = useRef(null);
  const [content, setContent] = useState('');
  const [images, setImage] = useState([]);
  const [signIn, setSignIn] = useState({});

  const clickHandler = () => setToWritePost(!toWritePost);
  const contentHandler = (event) => setContent(event.target.value);
  const imageHandler = (event) => setImage([...event.target.files]);
  const buttonHandler = (event) => {
    event.target.innerText === '작성' ? writePost(signIn) 
                                      : setToWritePost(!toWritePost);
  }
  const writePost = (input) => {
    const formData = new FormData();
    const body = {'content': content,
                  'user': input}
    formData.set('request', body);
    for (let i = 0; i < images.length; i++) {
      formData.set(`imageFiles[${i}]`, images[i])
    }
    // formData.set('imageFiles', images);
    console.log(formData.get('imageFiles'))
    fetch('http://localhost:8090/post/write',
          {method: 'POST',
           body: formData
          });
    clickHandler();
  }

  useEffect(() => {
    fetch('http://localhost:8090/post/check/token',
          {headers: {Authorization: token.token}})
        .then(response => response.json())
        .then(data => setSignIn({...JSON.parse(data['signIn'])}))
        .catch(err => console.error(err))

  }, [])

  return (
    <>
    <div className='fixed bg-grey/80 z-10 w-full h-full mt-16 ' onClick={clickHandler} />
    {signIn['userid']? <div className='fixed bg-white z-20 mt-24 w-4/5 left-9 content-center'>
                          <textarea ref={ref} onChange={contentHandler} className='my-8 p-2 w-4/5' id="" cols="30" rows="10" placeholder='무엇을 작성하시겠습니까?' />
                          <input ref={ref} onChange={imageHandler} className='files my-2 w-4/5' type="file" name="files" multiple='multiple' accept="image/png, image/jpg"/>
                          <div className='buttons flex w-4/5 justify-around mx-8 my-4'>
                            <div className="button w-16 p-2 bg-water text-black font-semibold rounded-lg shadow-md" onClick={buttonHandler}>작성</div>
                            <div className="button w-16 p-2 bg-grey text-black font-semibold rounded-lg shadow-md" onClick={buttonHandler}>취소</div>
                          </div>
                        </div>
                      : <ErrorMessage err={signIn['error']} />}
    </>
  )
}

export default CommuWrite